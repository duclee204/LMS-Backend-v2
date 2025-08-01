package org.example.lmsbackend.util;

import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoInfo;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class VideoMetadataExtractor {
    
    /**
     * Extract video metadata including duration
     * @param videoFile The video file
     * @return Map containing metadata (duration in seconds, width, height, etc.)
     */
    public static Map<String, Object> extractMetadata(File videoFile) {
        Map<String, Object> metadata = new HashMap<>();
        
        try {
            MultimediaObject multimediaObject = new MultimediaObject(videoFile);
            MultimediaInfo info = multimediaObject.getInfo();
            
            if (info != null) {
                // Duration in seconds
                long durationMillis = info.getDuration();
                int durationSeconds = (int) (durationMillis / 1000);
                metadata.put("duration", durationSeconds);
                metadata.put("durationMillis", durationMillis);
                
                // Video specific info
                VideoInfo videoInfo = info.getVideo();
                if (videoInfo != null) {
                    metadata.put("width", videoInfo.getSize() != null ? videoInfo.getSize().getWidth() : 0);
                    metadata.put("height", videoInfo.getSize() != null ? videoInfo.getSize().getHeight() : 0);
                    metadata.put("frameRate", videoInfo.getFrameRate());
                    metadata.put("bitRate", videoInfo.getBitRate());
                    metadata.put("decoder", videoInfo.getDecoder());
                }
                
                // Format info
                metadata.put("format", info.getFormat());
                
                System.out.println("📹 Video metadata extracted successfully:");
                System.out.println("  Duration: " + durationSeconds + " seconds");
                System.out.println("  Format: " + info.getFormat());
                if (videoInfo != null && videoInfo.getSize() != null) {
                    System.out.println("  Resolution: " + videoInfo.getSize().getWidth() + "x" + videoInfo.getSize().getHeight());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error extracting video metadata: " + e.getMessage());
            e.printStackTrace();
            // Set default values if extraction fails
            metadata.put("duration", 0);
            metadata.put("error", e.getMessage());
        }
        
        return metadata;
    }
    
    /**
     * Extract only duration from video file
     * @param videoFile The video file
     * @return Duration in seconds, or 0 if extraction fails
     */
    public static int extractDuration(File videoFile) {
        Map<String, Object> metadata = extractMetadata(videoFile);
        return (Integer) metadata.getOrDefault("duration", 0);
    }
}
