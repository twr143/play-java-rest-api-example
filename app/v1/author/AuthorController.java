package v1.author;

import play.libs.Json;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import v1.post.PostResource;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

/**
 * Created by ilya on 04.06.2017.
 */
public class AuthorController extends Controller {

  private final AuthorRepo repository;
  private final HttpExecutionContext ec;

  @Inject
  public AuthorController(AuthorRepo repository, HttpExecutionContext ec) {
    this.repository = repository;
    this.ec = ec;
  }
  public CompletionStage<Result> addPost(String id, String postId) {
    return repository.addPost(Long.parseLong(id),Long.parseLong(postId)).thenApplyAsync(result -> {
         String reason;
         if (result==1)
           reason="no author: "+ id;
         else if (result==2)
           reason="no post: "+ postId;
         else if (result==3)
           reason="author already has the post: "+ postId;
         else
           reason="added to author:"+id+" post:"+postId;
        return ok(Json.toJson(reason));
    }, ec.current());
  }
  public CompletionStage<Result> list() {
        return repository.listAuthors().thenApplyAsync(authors -> {
            final List<Author> authorsL = authors.collect(Collectors.toList());
            return ok(Json.toJson(authorsL));
        }, ec.current());
    }

}
