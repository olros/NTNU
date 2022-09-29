package Database.HibernateClasses;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Hibernate database class for the table TAGS
 */
@Entity
@Table(name = "TAGS")
public class Tags implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private int id;

	@Column(name = "tag")
	private String tag;

	@Column(name = "photo_id")
	private int photoId;

	public Tags() {
	}

	public Tags(String tag, int photoId) {
		this.tag = tag;
		this.photoId = photoId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getPhotoId() {
		return photoId;
	}

	public void setPhotoId(int photoId) {
		this.photoId = photoId;
	}

	/**
	 * Equals method for tags
	 * Tags are equal if they have the same String tag (the text in the tag) and int photo_id (the id of the photo it belong to)
	 *
	 * @param o an object o
	 * @return boolean true/false, depending on if the object is equal to the tag or not.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Tags)) {
			return false;
		}
		Tags tags = (Tags) o;
		return this.getPhotoId() == tags.getPhotoId() &&
				this.getTag().equals(tags.getTag());
	}

	/**
	 * Generates hashcode for the tag, based on the attributes used in the equals method
	 *
	 * @return the hashcode for the tag
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.getTag(), this.getPhotoId());
	}
}
