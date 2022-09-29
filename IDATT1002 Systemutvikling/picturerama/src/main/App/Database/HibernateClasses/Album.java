package Database.HibernateClasses;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Hibernate database class for the table ALBUMS
 */
@Entity
@Table(name = "ALBUMS")
public class Album implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private int id;
	@Column(name = "name")
	private String name;
	@Column(name = "user_id")
	private int userId;
	@ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
	@JoinTable(
			name = "ALBUMPHOTO",
			joinColumns = {@JoinColumn(name = "album_id", nullable = false, insertable = false)},
			inverseJoinColumns = {@JoinColumn(name = "photo_id")}
	)
	private Set<Photo> photos = new HashSet<>();

	public Album() {
	}

	public Album(String name, int userId) {
		this.name = name;
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Set<Photo> getPhotos() {
		return photos;
	}
}
