package v1.author;

import net.jodah.failsafe.CircuitBreaker;
import play.db.jpa.JPAApi;
import v1.post.PostData;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * Created by ilya on 04.06.2017.
 */
public class JPAAuthorRepo implements AuthorRepo {

  private final JPAApi jpaApi;
  private final AuthorExecutionContext ec;
  private final CircuitBreaker circuitBreaker = new CircuitBreaker().withFailureThreshold(10).withSuccessThreshold(3);
  @Inject
  public JPAAuthorRepo(JPAApi jpaApi, AuthorExecutionContext ec) {
    this.jpaApi = jpaApi;
    this.ec = ec;
  }

  @Override
  public CompletionStage<Stream<Author>> listAuthors() {
    return supplyAsync(() -> {
      return wrap(em->select(em));
    }, ec);
  }
  @Override
  public CompletionStage<Author> create(Author postData) {
    return null;
  }
  @Override
  public CompletionStage<Optional<Author>> get(Long id) {
    return null;
  }
  @Override
  public CompletionStage<Integer> addPost(Long authorId, Long postId) {
    return supplyAsync(() -> {
          return wrap(em->{
            Author a = em.find(Author.class, authorId);
            if (a==null)
              return 1;
            PostData pd = em.find(PostData.class, postId);
            if (pd==null)
              return 2;
            pd.setAuthor(a);
             em.merge(pd);
             return 0;
          });
        }, ec);  }

  private Stream<Author> select(EntityManager em) {
    TypedQuery<Author> query = em.createQuery("SELECT a FROM Author a", Author.class);
    return query.getResultList().stream();
  }

  private <T> T wrap(Function<EntityManager, T> function) {
      return jpaApi.withTransaction(function);
  }

}
