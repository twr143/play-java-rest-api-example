package v1.post;

import net.jodah.failsafe.*;
import play.cache.CacheApi;
import play.cache.NamedCache;
import play.db.jpa.JPAApi;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.concurrent.CompletableFuture.supplyAsync;

/**
 * A repository that provides a non-blocking API with a custom execution context
 * and circuit breaker.
 */
@Singleton
public class JPAPostRepository implements PostRepository {

    private final JPAApi jpaApi;
    private final PostExecutionContext ec;
      private final CircuitBreaker circuitBreaker = new CircuitBreaker().withFailureThreshold(10).withSuccessThreshold(3);
    protected CacheApi repoCache;
    private static final String postsList="postsList";

    @Inject
    public JPAPostRepository(JPAApi api, PostExecutionContext ec, @NamedCache("post-repo-cache")CacheApi repoCache) {
        this.jpaApi = api;
        this.ec = ec;
        this.repoCache = repoCache;
    }

    @Override
    public CompletionStage<Stream<PostData>> listPosts() {
        return supplyAsync(() -> wrap(em -> select(em)), ec);
    }

    @Override
    public CompletionStage<PostData> create(PostData postData) {
        return supplyAsync(() -> wrap(em -> insert(em, postData)), ec);
    }

    @Override
    public CompletionStage<Optional<PostData>> get(Long id) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> lookup(em, id))), ec);
    }

    @Override
    public CompletionStage<Optional<PostData>> update(Long id, PostData postData) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> modify(em, id, postData))), ec);
    }
    @Override
    public CompletionStage<Integer> remove(long id) {
        return supplyAsync(() -> wrap(em -> Failsafe.with(circuitBreaker).get(() -> {
          int result = remove(em, id);
          if (result==0)//success
              repoCache.remove(postsList);
          return result;

        })), ec);
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }

    private Optional<PostData> lookup(EntityManager em, Long id) throws SQLException {
//        throw new SQLException("Call this to cause the circuit breaker to trip");
        return Optional.ofNullable(em.find(PostData.class, id));
    }

    private Stream<PostData> select(EntityManager em) {
        List<PostData> cachedPosts =  repoCache.get(postsList);
        if (cachedPosts!=null)
            return cachedPosts.stream();
        TypedQuery<PostData> query = em.createQuery("SELECT p FROM PostData p", PostData.class);
        cachedPosts=query.getResultList();
        repoCache.set(postsList,cachedPosts);
        return cachedPosts.stream();
    }

    private Optional<PostData> modify(EntityManager em, Long id, PostData postData) throws InterruptedException {
        final PostData data = em.find(PostData.class, id);
        if (data != null) {
            data.title = postData.title;
            data.body = postData.body;
        }
//        Thread.sleep(10000L);
        return Optional.ofNullable(data);
    }

    private PostData insert(EntityManager em, PostData postData) {
        PostData resultPost = em.merge(postData);
        repoCache.remove(postsList);
        return resultPost;
    }

    private int remove(EntityManager em, long id){
        PostData pd= em.find(PostData.class,id);
        if (pd==null)
            return 1;
        em.remove(pd);
        return 0;
    }
}
