package com.snhu.weighttracker;

import android.content.Context;
import androidx.room.Room;

public class UserRepository {
    private static UserRepository userRepo;
    private final UserDao userDao;

    // Instantiate or get the repository singleton
    public static UserRepository getInstance(Context context) {
        if (userRepo == null) {
            userRepo = new UserRepository(context);
        }

        return userRepo;
    }

    private UserRepository(Context context) {
        UserDatabase userDatabase = Room.databaseBuilder(context, UserDatabase.class,
                "user.db").allowMainThreadQueries().build();

        userDao = userDatabase.userDao();
    }

    public void addUser(User user) {
        userDao.addUser(user);
    }

    public int userExists(String username) {
        return userDao.userExists(username);
    }
}
