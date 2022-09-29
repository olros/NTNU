package Css;

/**
 * Enum FeedbackType is the type of feed back the user receives
 * The class is used inn css methods to obtain a color based on the feedback type.
 * ERROR gives a red color
 * SUCCESSFUL gives a green color
 */
public enum FeedbackType {

	ERROR("red"), SUCCESSFUL("green");

	private String color;

	FeedbackType(String color) {
		this.color = color;
	}

	String getColor() {
		return this.color;
	}
}
