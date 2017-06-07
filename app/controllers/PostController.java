package controllers;

import play.data.Form;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Result;
import v1.post.PostData;
import v1.post.PostResource;
import v1.post.PostResourceHandler;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Created by ilya on 05.06.2017.
 */
public class PostController extends v1.post.PostController{

  protected FormFactory formFactory;

  @Inject
  public PostController(HttpExecutionContext ec, PostResourceHandler handler, FormFactory formFactory) {
    super(ec, handler);
    this.formFactory=formFactory;
  }
  public CompletionStage<Result> addPost() {

    Form<PostResource> form = this.formFactory.form(PostResource.class).bindFromRequest();
    if (!form.hasErrors()){
      PostResource post = form.get();
      return handler.create(post).thenApplyAsync(a->redirect("/posts"),ec.current());
    }
    return supplyAsync(()->redirect("/posts"),ec.current());
  }
  public CompletionStage<Result> listOnIndex() {
        return handler.find().thenApplyAsync(posts -> {
            final List<PostResource> postList = posts.collect(Collectors.toList());
            return index(postList);
        }, ec.current());
    }
  public CompletionStage<Result> removePost() {

    PostResource post = this.formFactory.form(PostResource.class).bindFromRequest().get();
    return handler.delete(post.getId()).thenApplyAsync(a->redirect("/posts"),ec.current());
  }

}
