package org.example.lmsbackend.utils;

import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import java.io.File;

public class VideoMetadataExtractor {
    
    /**
     * Extract metadata from video file
     * @param videoFile The video file to analyze
     * @return MultimediaInfo containing metadata, or null if extraction fails
     */
    public static MultimediaInfo extractMetadata(File videoFile) {
        try {
            MultimediaObject multimediaObject = new MultimediaObject(videoFile);
            return multimediaObject.getInfo();
        } catch (Exception e) {
            System.err.println("Error extracting video metadata: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Extract duration from video file
     * @param videoFile The video file to analyze
     * @return Duration in seconds, or 0 if extraction fails
     */
    public static long extractDuration(File videoFile) {
        try {
            MultimediaInfo info = extractMetadata(videoFile);
            if (info != null) {
                // Duration is in milliseconds, convert to seconds
                long durationInSeconds = info.getDuration() / 1000;
                System.out.println("📹 Video duration extracted: " + durationInSeconds + " seconds");
                return durationInSeconds;
            }
        } catch (Exception e) {
            System.err.println("Error extracting video duration: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Extract duration from video file path
     * @param videoFilePath The path to the video file
     * @return Duration in seconds, or 0 if extraction fails
     */
    public static long extractDuration(String videoFilePath) {
        return extractDuration(new File(videoFilePath));
    }
}
