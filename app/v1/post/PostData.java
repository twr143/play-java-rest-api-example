package v1.post;

import v1.author.Author;

import javax.persistence.*;

/**
 * Data returned from the database
 */
@Entity
@Table(name = "posts")
public class PostData {

    public PostData() {
    }

    public PostData(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    public Long id;
    public String title;
    public String body;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="author_id")
    private Author author;
    public Author getAuthor() {
      return author;
    }
  public void setAuthor(Author author) {
    this.author = author;
  }
  @Override
  public String toString() {
    return "PostData{" +
            "id=" + id +
            ", title='" + title + '\'' +
            ", body='" + body + '\'' +
            '}';
  }
}
