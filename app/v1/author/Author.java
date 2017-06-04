package v1.author;

import v1.post.PostData;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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
  Set<PostData> posts;
  private String name;
  public Author(String name) {
    this();
    this.name = name;
  }
  public Author() {
  }

  public String getName() {
    return name;
  }
  public Long getId() {
    return id;
  }
  public String getPostsStr() {
    String result="";
    for (PostData p:posts)
      result+=p.toString()+" ";
    return result;
  }
  @Override
  public String toString() {
    return "Author{" +
            "id=" + id +
            ", name='" + name + '\'' +
            '}';
  }
}
