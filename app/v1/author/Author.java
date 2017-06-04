package v1.author;

import v1.post.PostData;

import javax.persistence.*;
import java.util.List;

/**
 * Created by ilya on 04.06.2017.
 */
@Entity
@Table(name = "authors")
public class Author {
  @Id
  @GeneratedValue(strategy= GenerationType.AUTO)
  private Long id;

  @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL, mappedBy = "author")
  List<PostData> posts;
  private String name;
  public Author(String name) {
    this();
    this.name = name;
  }
  public Author() {
  }

  public void addPost(PostData post){
    posts.add(post);
  }
  public String getName() {
    return name;
  }
  public Long getId() {
    return id;
  }
}
