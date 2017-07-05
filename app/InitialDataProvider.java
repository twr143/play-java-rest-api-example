import com.google.inject.Inject;
import play.Environment;
import play.db.jpa.JPAApi;
import play.inject.ApplicationLifecycle;
import play.libs.Yaml;
import v1.author.Author;
import v1.post.PostData;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Created by ilya on 04.06.2017.
 */
public class InitialDataProvider implements InitialData {
  private final JPAApi jpaApi;
  private final play.Environment environment;
  private final ApplicationLifecycle lifecycle;
  @Inject
  public InitialDataProvider(JPAApi jpaApi, Environment environment, ApplicationLifecycle lifecycle) {
    this.environment=environment;
    this.jpaApi = jpaApi;
    this.lifecycle = lifecycle;
    int i=0;
             if (this.environment.isDev()) {
               wrap(this::initMockData);
             }
             this.lifecycle.addStopHook(()->{
               System.out.println("stop from initial data provider");
                       return CompletableFuture.completedFuture(null);
             });

     }
     private <T> T initMockData(EntityManager em){
       Long count = countPostData(em);
       if (count == 0) {
         InputStream is = this.getClass().getClassLoader().getResourceAsStream("initial-data.yml");

         @SuppressWarnings("unchecked")
         Map<String, List<Object>> all =
                 (Map<String, List<Object>>) Yaml.load(is, this.getClass().getClassLoader());

         List<Object> posts = all.get("posts");
         posts.forEach(p -> {
           @SuppressWarnings("unchecked")
           LinkedHashMap<String, String> entry = (LinkedHashMap<String, String>) ((Map.Entry) (((LinkedHashMap) p).entrySet().iterator().next())).getValue();
           em.merge(new PostData(entry.get("title"), entry.get("body")));
         });
         List<Object> authors = all.get("authors");
         authors.forEach(p -> {
           Map.Entry<String, String> entry = (Map.Entry) (((LinkedHashMap) p).entrySet().iterator().next());
           em.merge(new Author(entry.getValue()));
         });
       }
       return null;

     }
  private <T> T wrap(Function<EntityManager, T> function) {
      return jpaApi.withTransaction(function);
  }
   private Long countPostData(EntityManager em){
     CriteriaBuilder qb = em.getCriteriaBuilder();
     CriteriaQuery<Long> cq = qb.createQuery(Long.class);
     cq.select(qb.count(cq.from(PostData.class)));
//     cq.where(/*your stuff*/);
     return em.createQuery(cq).getSingleResult();
   }
}
