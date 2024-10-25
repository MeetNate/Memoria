package Helper;

import java.util.List;
import java.util.Map;

import Model.Memory;
import Model.Message;

public class InterfaceHelper {
    // Interface to handle classroom checks
    interface OnClassroomCheckListener {
        void onClassroomCheck(String classroomId);
    }
    public interface OnMessagesFetchedListener {
        void onMessagesFetched(List<Message> messages);
        void onError(String errorMessage);
    }

    // Interface for callback
    public interface OnMemoriesFetchListener {
        void onMemoriesFetched(List<Memory> memories);
    }

    public interface OnAchievementsFetchedListener {
        void onAchievementsFetched(List<Map<String, String>> achievements);
        void onError(String error);
    }

    // Listener interface for achievement deletion events
    public interface OnAchievementDeletedListener {
        void onAchievementDeleted();
        void onError(String error);
    }

    public interface OnSportsFetchedListener{
        void onSportsFetched(List<Map<String, String>> achievements);
        void onError(String error);
    }

    // Listener interface for deletion events
    public interface OnSportDeletedListener {
        void onSportDeleted();
        void onError(String error);
    }

    // Define the listener interface
    public interface OnSubjectDeletedListener {
        void onSubjectDeleted();
        void onError(String error);
    }

    // New interface for fetching subjects
    public interface OnSubjectsFetchedListener {
        void onSubjectsFetched(List<Map<String, String>> subjects);
        void onError(String error);
    }

    public interface OnResultDeletedListener {
        void onResultDeleted();
        void onError(String error);
    }

    // Interface for fetching results with correct data types
    public interface OnResultsFetchedListener {
        void onResultsFetched(List<Map<String, Object>> results);  // Map should handle both String (grade) and Double (percentage)
        void onError(String error);
    }

    // Interface for the callback to handle the response
    public interface ClassroomDetailsCallback {
        void onCallback(Map<String, String> classroomDetails);
    }

}
