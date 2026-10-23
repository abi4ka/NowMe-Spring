package com.abik.nowme.module.shared.seed;

import com.abik.nowme.module.nowme.entity.Comment;
import com.abik.nowme.module.nowme.entity.Nowme;
import com.abik.nowme.module.nowme.entity.NowmeLike;
import com.abik.nowme.module.nowme.repository.CommentRepository;
import com.abik.nowme.module.nowme.repository.NowmeLikeRepository;
import com.abik.nowme.module.nowme.repository.NowmeRepository;
import com.abik.nowme.module.shared.repository.RefreshTokenRepository;
import com.abik.nowme.module.user.Visibility;
import com.abik.nowme.module.user.entity.Follow;
import com.abik.nowme.module.user.entity.User;
import com.abik.nowme.module.user.repository.UserFollowRepository;
import com.abik.nowme.module.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Fills an empty database with demo users, follows, posts, likes and comments.
 * Enabled by {@code app.seed.enabled}; {@code app.seed.reset} wipes all data first.
 * All dates are relative to the moment of seeding.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class InitialDataSeeder implements ApplicationRunner {

    static final String HERO_USERNAME = "nowme_star";
    static final String HERO_PASSWORD = "Star2026!";
    static final String DEFAULT_PASSWORD = "password123";

    private static final Path UPLOADS_DIR = Paths.get("uploads");
    private static final String SEED_FILE_PREFIX = "seed_";
    private static final int HERO_HISTORY_DAYS = 120;
    private static final int HERO_UNBROKEN_DAYS = 35;

    // One caption per bundled photo, index i describes photo_(i+1).jpg.
    private static final String[] PHOTO_CAPTIONS = {
            "Morning view that makes the alarm worth it",
            "Fog rolling over the lake, total silence",
            "Found a hidden waterfall on today's hike",
            "Salt air and cold rocks",
            "Took the long way home through the forest",
            "Lying in the grass doing absolutely nothing",
            "River valley after the rain",
            "Made it above the clouds. Legs are gone",
            "Cliffside walk, wind at full volume",
            "Office for today: big table, bigger coffee",
            "City lights from the bridge",
            "White walls, blue domes, zero plans",
            "Clouds were showing off today",
            "Empty street at 7am, my favourite city hour",
            "Lighthouse keeper vibes",
            "This door has seen things",
            "Gaming night, nobody is sleeping",
            "Concrete and sky",
            "Golden hour hit different today",
            "Clean desk, clean mind (for about an hour)",
            "Late walk by the river",
            "Sunset bench with my favourite person",
            "Balcony flowers finally bloomed",
            "Canals and gingerbread houses",
            "Summit or nothing",
            "Dunes after dark",
            "Feels like a movie set",
            "Met the fluffiest local",
            "Slow tea kind of afternoon",
            "New roommate. He eats socks",
            "Old camera, new roll of film",
            "Cooking something that will probably work",
            "Honey straight from the farm",
            "Lemon tea and a long book",
            "Hood up, phone down",
            "Raspberry season is the best season",
            "Latte art attempt #47",
            "Kept a very safe distance, promise",
            "Salad that actually tastes good",
            "Breakfast bowl, 10/10"
    };

    private static final String[] GENERIC_CAPTIONS = {
            "Today in one picture",
            "Small things, good day",
            "Not much happened, and that's fine",
            "Saving this moment",
            "Weekend mode: on",
            "Couldn't not take a photo",
            "Note to self: do this more often",
            "Real life, no filter"
    };

    private static final String[] COMMENTS = {
            "Wow, where is this?",
            "Okay this is beautiful",
            "Take me with you next time!",
            "This is so you 😄",
            "Need this view right now",
            "Love the colours",
            "Wallpaper material",
            "Jealous. Very jealous.",
            "Looks so peaceful",
            "Ahh I miss this place",
            "Saving this for inspiration",
            "Is that a new camera? The quality!",
            "How was it?",
            "We should go together",
            "This made my day",
            "Iconic",
            "That light though",
            "10/10 would visit",
            "You always find the best spots",
            "Recipe please!",
            "Cozy vibes",
            "Can't believe this is real",
            "Haha love it",
            "Goals",
            "Send me the location!"
    };

    private static final String[] HERO_REPLIES = {
            "Thank you! 🙌",
            "Haha right?",
            "Next time you're coming with me",
            "Sending you the location",
            "It was even better in person",
            "Deal!",
            "Thanks, it was a good day"
    };

    private static final String LONG_COMMENT = """
            Okay I have to say something about this one.
            Every time you post from this place I promise myself I'll go, and every time I don't.
            Not anymore. Booking the trip this weekend.
            Any tips on where to stay? And is the coffee place from your last post still open?""";

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final NowmeRepository nowmeRepository;
    private final NowmeLikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.reset:false}")
    private boolean reset;

    private final Random random = new Random(42);
    private final Set<String> follows = new HashSet<>();
    private LocalDateTime now;
    private List<String> images;
    private int nextImage;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        if (reset) {
            wipeData();
        } else if (userRepository.count() > 0) {
            log.info("Seed: database is not empty, skipping initial data (set SEED_RESET=true to reseed)");
            return;
        }

        now = LocalDateTime.now();
        images = copyPhotos();

        // Users
        String heroPasswordHash = passwordEncoder.encode(HERO_PASSWORD);
        String defaultPasswordHash = passwordEncoder.encode(DEFAULT_PASSWORD);
        User hero = user(HERO_USERNAME, "🚀", 150, heroPasswordHash);
        User anna = user("anna_k", "🌸", 140, defaultPasswordHash);
        User leo = user("leo.travels", "✈️", 130, defaultPasswordHash);
        User mia = user("mia_cooks", "🍜", 120, defaultPasswordHash);
        User sam = user("sam_runs", "🏃", 110, defaultPasswordHash);
        User zoe = user("zoe.art", "🎨", 100, defaultPasswordHash);
        User ben = user("ben_photo", "📷", 90, defaultPasswordHash);
        User kate = user("kate_music", "🎧", 80, defaultPasswordHash);
        User tom = user("fan_tom", "⚽", 60, defaultPasswordHash);
        User lucy = user("lucy_reads", "📚", 45, defaultPasswordHash);
        User olga = user("olga_dev", "💻", 30, defaultPasswordHash);
        User newbie = user("newbie_joe", "🐣", 1, defaultPasswordHash);
        User ghost = user("ghost_user", "👻", 70, defaultPasswordHash);
        ghost.setActive(false);
        List<User> users = List.of(hero, anna, leo, mia, sam, zoe, ben, kate, tom, lucy, olga, newbie, ghost);
        userRepository.saveAll(users);

        // Follows: hero has 5 friends, follows 2 people who don't follow back, and is followed by 3 (+1 inactive)
        List<Follow> followRows = new ArrayList<>();
        for (User friend : List.of(anna, leo, mia, sam, zoe)) {
            friends(followRows, hero, friend);
        }
        follow(followRows, hero, ben);
        follow(followRows, hero, kate);
        for (User follower : List.of(tom, lucy, olga, ghost)) {
            follow(followRows, follower, hero);
        }
        friends(followRows, anna, leo);
        friends(followRows, anna, mia);
        friends(followRows, anna, zoe);
        friends(followRows, leo, sam);
        friends(followRows, mia, zoe);
        friends(followRows, kate, ben);
        friends(followRows, lucy, mia);
        friends(followRows, olga, sam);
        follow(followRows, sam, ben);
        follow(followRows, leo, ben);
        follow(followRows, tom, anna);
        follow(followRows, tom, sam);
        follow(followRows, lucy, zoe);
        follow(followRows, olga, kate);
        follow(followRows, kate, zoe);
        follow(followRows, zoe, kate);
        followRepository.saveAll(followRows);

        // Posts
        List<Nowme> heroPosts = heroPosts(hero);
        List<Nowme> otherPosts = new ArrayList<>();
        otherPosts.addAll(recentPosts(anna, 0.75, 0.2));
        otherPosts.addAll(recentPosts(leo, 0.65, 0.2));
        otherPosts.addAll(recentPosts(mia, 0.7, 0.25));
        otherPosts.addAll(recentPosts(sam, 0.55, 0.1));
        otherPosts.addAll(recentPosts(zoe, 0.6, 0.3));
        otherPosts.addAll(recentPosts(ben, 0.7, 0.5));
        otherPosts.addAll(recentPosts(kate, 0.5, 0.2));
        otherPosts.addAll(recentPosts(tom, 0.4, 0.2));
        otherPosts.addAll(recentPosts(lucy, 0.45, 0.2));
        otherPosts.addAll(recentPosts(olga, 0.35, 0.2));
        otherPosts.addAll(recentPosts(ghost, 0.5, 0.0));
        nowmeRepository.saveAll(heroPosts);
        nowmeRepository.saveAll(otherPosts);

        List<User> activeUsers = List.of(hero, anna, leo, mia, sam, zoe, ben, kate, tom, lucy, olga, newbie);
        List<Nowme> allPosts = new ArrayList<>(heroPosts);
        allPosts.addAll(otherPosts);

        // Likes
        List<NowmeLike> likes = new ArrayList<>();
        for (Nowme post : allPosts) {
            if (!post.isActive()) {
                continue;
            }
            double chance = likeChance(post);
            for (User viewer : activeUsers) {
                if (viewer != post.getUser() && canSee(viewer, post) && random.nextDouble() < chance) {
                    likes.add(NowmeLike.builder().user(viewer).nowme(post).build());
                }
            }
        }
        likeRepository.saveAll(likes);

        // Comments
        List<Comment> comments = new ArrayList<>();
        Nowme heroYesterday = null;
        Nowme heroToday = null;
        Nowme latestPinned = null;
        for (Nowme post : heroPosts) {
            if (!post.isActive()) {
                continue;
            }
            int age = ageInDays(post);
            if (age == 0 && heroToday == null) {
                heroToday = post;
            }
            if (age == 1 && heroYesterday == null) {
                heroYesterday = post;
            }
            if (post.getFavorite() && (latestPinned == null || post.getCreationTime().isAfter(latestPinned.getCreationTime()))) {
                latestPinned = post;
            }
        }
        for (Nowme post : heroPosts) {
            if (!post.isActive()) {
                continue;
            }
            int age = ageInDays(post);
            int count;
            if (post == heroYesterday || post == latestPinned) {
                count = 24;
            } else if (post.getFavorite()) {
                count = 3 + random.nextInt(6);
            } else if (age < 7) {
                count = 2 + random.nextInt(5);
            } else if (age < 30 && random.nextDouble() < 0.3) {
                count = 1 + random.nextInt(3);
            } else {
                count = 0;
            }
            addThread(comments, post, count, activeUsers, true);
        }
        for (Nowme post : otherPosts) {
            if (post.isActive() && ageInDays(post) < 14) {
                addThread(comments, post, random.nextInt(4), activeUsers, false);
            }
        }
        if (heroYesterday != null) {
            comments.add(comment(zoe, heroYesterday, LONG_COMMENT, heroYesterday.getCreationTime().plusMinutes(5)));
        }
        if (heroToday != null) {
            Comment deleted = comment(tom, heroToday, "Oops, wrong post", heroToday.getCreationTime().plusMinutes(3));
            deleted.setActive(false);
            comments.add(deleted);
        }
        commentRepository.saveAll(comments);

        log.info("Seed: created {} users, {} follows, {} posts, {} likes, {} comments",
                users.size(), followRows.size(), allPosts.size(), likes.size(), comments.size());
        log.info("Seed: main demo account -> username '{}', password '{}' (other accounts use '{}')",
                HERO_USERNAME, HERO_PASSWORD, DEFAULT_PASSWORD);
    }

    private void wipeData() throws IOException {
        log.info("Seed: SEED_RESET=true, deleting all existing data");
        commentRepository.deleteAllInBatch();
        likeRepository.deleteAllInBatch();
        nowmeRepository.deleteAllInBatch();
        followRepository.deleteAllInBatch();
        refreshTokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        if (Files.isDirectory(UPLOADS_DIR)) {
            try (DirectoryStream<Path> seeded = Files.newDirectoryStream(UPLOADS_DIR, SEED_FILE_PREFIX + "*")) {
                for (Path file : seeded) {
                    Files.deleteIfExists(file);
                }
            }
        }
    }

    private List<String> copyPhotos() throws IOException {
        Resource[] photos = new PathMatchingResourcePatternResolver().getResources("classpath:seed/photos/photo_*.jpg");
        Arrays.sort(photos, Comparator.comparing(Resource::getFilename));
        Files.createDirectories(UPLOADS_DIR);
        List<String> fileNames = new ArrayList<>();
        for (Resource photo : photos) {
            String fileName = SEED_FILE_PREFIX + photo.getFilename();
            try (InputStream in = photo.getInputStream()) {
                Files.copy(in, UPLOADS_DIR.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
            }
            fileNames.add(fileName);
        }
        if (fileNames.isEmpty()) {
            throw new IllegalStateException("No seed photos found in classpath:seed/photos");
        }
        return fileNames;
    }

    private User user(String username, String avatar, int registeredDaysAgo, String passwordHash) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordHash);
        user.setAvatar(avatar);
        user.setRegisterTime(now.minusDays(registeredDaysAgo).withHour(12).withMinute(0).withSecond(0).withNano(0));
        return user;
    }

    private void follow(List<Follow> rows, User follower, User following) {
        if (follows.add(follower.getUsername() + "->" + following.getUsername())) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            rows.add(follow);
        }
    }

    private void friends(List<Follow> rows, User a, User b) {
        follow(rows, a, b);
        follow(rows, b, a);
    }

    private boolean isFollowing(User follower, User following) {
        return follows.contains(follower.getUsername() + "->" + following.getUsername());
    }

    private boolean areFriends(User a, User b) {
        return isFollowing(a, b) && isFollowing(b, a);
    }

    // Mirrors the feed rules: public posts for followers, friends-only posts for mutual follows.
    private boolean canSee(User viewer, Nowme post) {
        User author = post.getUser();
        if (post.getVisibility() == Visibility.FRIENDS_ONLY) {
            return areFriends(viewer, author);
        }
        return isFollowing(viewer, author) || areFriends(viewer, author);
    }

    private List<Nowme> heroPosts(User hero) {
        List<Nowme> posts = new ArrayList<>();
        Set<Integer> pinnedDays = Set.of(15, 32, 48, 65, 83, 104);
        for (int day = 0; day < HERO_HISTORY_DAYS; day++) {
            boolean pinnedDay = pinnedDays.contains(day);
            // A 4-day break right before the unbroken run, so the streak shows HERO_UNBROKEN_DAYS
            boolean vacation = day >= HERO_UNBROKEN_DAYS && day < HERO_UNBROKEN_DAYS + 4;
            if (vacation || (day >= HERO_UNBROKEN_DAYS && !pinnedDay && random.nextDouble() < 0.15)) {
                continue;
            }
            int postsToday = random.nextDouble() < 0.08 ? 2 + random.nextInt(2) : 1;
            for (int i = 0; i < postsToday; i++) {
                Nowme post = post(hero, day, 0.25);
                if (pinnedDay && i == 0) {
                    post.setFavorite(true);
                    post.setVisibility(Visibility.PUBLIC);
                }
                posts.add(post);
            }
        }
        Nowme deleted = post(hero, 3, 0.0);
        deleted.setDescription("Blurry photo, deleting this one");
        deleted.setActive(false);
        posts.add(deleted);
        return posts;
    }

    private List<Nowme> recentPosts(User author, double dailyChance, double friendsOnlyChance) {
        List<Nowme> posts = new ArrayList<>();
        for (int day = 0; day < 14; day++) {
            if (random.nextDouble() < dailyChance) {
                posts.add(post(author, day, friendsOnlyChance));
            }
        }
        int pinned = 1 + random.nextInt(2);
        for (int i = 0; i < pinned; i++) {
            Nowme post = post(author, 20 + random.nextInt(40), 0.0);
            post.setFavorite(true);
            posts.add(post);
        }
        return posts;
    }

    private Nowme post(User author, int daysAgo, double friendsOnlyChance) {
        int photo = nextImage++ % images.size();
        Nowme post = new Nowme();
        post.setUser(author);
        post.setImage(images.get(photo));
        post.setCreationTime(timeOnDay(daysAgo));
        post.setVisibility(random.nextDouble() < friendsOnlyChance ? Visibility.FRIENDS_ONLY : Visibility.PUBLIC);
        double captionRoll = random.nextDouble();
        if (captionRoll < 0.7 && photo < PHOTO_CAPTIONS.length) {
            post.setDescription(PHOTO_CAPTIONS[photo]);
        } else if (captionRoll < 0.9) {
            post.setDescription(pick(GENERIC_CAPTIONS));
        }
        return post;
    }

    private LocalDateTime timeOnDay(int daysAgo) {
        LocalDateTime time = now.minusDays(daysAgo)
                .withHour(8 + random.nextInt(15))
                .withMinute(random.nextInt(60))
                .withSecond(random.nextInt(60))
                .withNano(0);
        if (time.isAfter(now)) {
            int minutesSinceMidnight = now.getHour() * 60 + now.getMinute();
            time = now.minusMinutes(random.nextInt(Math.min(180, minutesSinceMidnight) + 1));
        }
        return time;
    }

    private double likeChance(Nowme post) {
        if (post.getFavorite()) {
            return 0.8;
        }
        int age = ageInDays(post);
        if (age < 7) {
            return 0.7;
        }
        return age < 30 ? 0.4 : 0.15;
    }

    private void addThread(List<Comment> comments, Nowme post, int count, List<User> users, boolean heroReplies) {
        List<User> commenters = users.stream()
                .filter(u -> u != post.getUser() && canSee(u, post))
                .toList();
        if (commenters.isEmpty()) {
            return;
        }
        LocalDateTime time = post.getCreationTime();
        for (int i = 0; i < count; i++) {
            time = nextCommentTime(time);
            User author = commenters.get(random.nextInt(commenters.size()));
            comments.add(comment(author, post, pick(COMMENTS), time));
            if (heroReplies && random.nextDouble() < 0.35 && i + 1 < count) {
                time = nextCommentTime(time);
                comments.add(comment(post.getUser(), post, "@" + author.getUsername() + " " + pick(HERO_REPLIES), time));
                i++;
            }
        }
    }

    private LocalDateTime nextCommentTime(LocalDateTime previous) {
        LocalDateTime next = previous.plusMinutes(2 + random.nextInt(90));
        return next.isAfter(now) ? now.minusSeconds(random.nextInt(60)) : next;
    }

    private Comment comment(User author, Nowme post, String content, LocalDateTime createdAt) {
        Comment comment = new Comment();
        comment.setUser(author);
        comment.setNowme(post);
        comment.setContent(content);
        comment.setCreatedAt(createdAt.isAfter(now) ? now : createdAt);
        return comment;
    }

    private int ageInDays(Nowme post) {
        return (int) ChronoUnit.DAYS.between(post.getCreationTime().toLocalDate(), now.toLocalDate());
    }

    private String pick(String[] values) {
        return values[random.nextInt(values.length)];
    }
}
