package com.example.safesleep;

import java.util.HashMap;
import java.util.Map;

public class CollaborativeFiltering {

    // User-item interaction data (user ID -> (item ID -> rating))
    private Map<String, Map<String, Integer>> userItemRatings;

    public CollaborativeFiltering() {
        userItemRatings = new HashMap<>();
    }

    // Function to add user-item ratings to the data
    public void addUserItemRating(String userId, String itemId, int rating) {
        userItemRatings.putIfAbsent(userId, new HashMap<>());
        userItemRatings.get(userId).put(itemId, rating);
    }

    // Function to get the cosine similarity between two users based on their item ratings
    private double cosineSimilarity(Map<String, Integer> ratings1, Map<String, Integer> ratings2) {
        // Calculate dot product
        double dotProduct = 0.0;
        for (String itemId : ratings1.keySet()) {
            if (ratings2.containsKey(itemId)) {
                dotProduct += ratings1.get(itemId) * ratings2.get(itemId);
            }
        }

        // Calculate magnitudes
        double magnitude1 = Math.sqrt(ratings1.values().stream().mapToDouble(r -> r * r).sum());
        double magnitude2 = Math.sqrt(ratings2.values().stream().mapToDouble(r -> r * r).sum());

        // Calculate cosine similarity
        return dotProduct / (magnitude1 * magnitude2);
    }

    // Function to get user-user similarity based on cosine similarity
    private Map<String, Double> getUserSimilarities(String targetUserId) {
        Map<String, Double> userSimilarities = new HashMap<>();

        if (userItemRatings.containsKey(targetUserId)) {
            Map<String, Integer> targetUserRatings = userItemRatings.get(targetUserId);

            for (String userId : userItemRatings.keySet()) {
                if (!userId.equals(targetUserId)) {
                    Map<String, Integer> otherUserRatings = userItemRatings.get(userId);
                    double similarity = cosineSimilarity(targetUserRatings, otherUserRatings);
                    userSimilarities.put(userId, similarity);
                }
            }
        }

        return userSimilarities;
    }

    // Function to get personalized recommendations for a target user
    public Map<String, Double> getRecommendations(String targetUserId) {
        Map<String, Double> recommendations = new HashMap<>();

        if (userItemRatings.containsKey(targetUserId)) {
            Map<String, Double> userSimilarities = getUserSimilarities(targetUserId);
            Map<String, Integer> targetUserRatings = userItemRatings.get(targetUserId);

            for (String userId : userSimilarities.keySet()) {
                if (userSimilarities.get(userId) > 0.0) {
                    Map<String, Integer> otherUserRatings = userItemRatings.get(userId);
                    for (String itemId : otherUserRatings.keySet()) {
                        if (!targetUserRatings.containsKey(itemId)) {
                            // Only recommend items not already rated by the target user
                            double weightedRating = userSimilarities.get(userId) * otherUserRatings.get(itemId);
                            recommendations.put(itemId, recommendations.getOrDefault(itemId, 0.0) + weightedRating);
                        }
                    }
                }
            }
        }

        return recommendations;
    }
}
