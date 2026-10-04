package com.examsecure.util;

import java.io.File;

public class FileStore {

    private static final String BASE_FOLDER =
            System.getProperty("user.home")
            + File.separator
            + "ExamSecurePapers";

    public static File get(String fileName) {

        if (fileName == null || fileName.isBlank()) {

            return new File(
                    BASE_FOLDER,
                    "invalid"
            );
        }

        // Prevent directory traversal
        String safeName =
                new File(fileName).getName();

        return new File(
                BASE_FOLDER,
                safeName
        );
    }

    public static File getBaseFolder() {

        File folder =
                new File(BASE_FOLDER);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }
}