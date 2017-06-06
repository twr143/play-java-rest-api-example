package v1.post;

import v1.author.Author;

/**
 * Resource for the API.  This is a presentation class for frontend work.
 */
public class PostResource {
    private String id;
    private String link;
    private String title;
    private String body;
    private String author;

    public PostResource() {
    }

    public PostResource(String id, String link, String title, String body, Author author) {
        this.id = id;
        this.link = link;
        this.title = title;
        this.body = body;
        this.author=author.toString();
    }

    public PostResource(PostData data, String link) {
        this.id = data.id.toString();
        this.link = link;
        this.title = data.title;
        this.body = data.body;
        this.author=data.getAuthor()!=null?data.getAuthor().toString():null;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getAuthor() {
      return author;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public void setId(String id) {
      this.id = id;
    }
}
