import com.google.inject.Inject;
import play.Application;
import play.Environment;
import play.api.Play;
import play.db.jpa.JPAApi;
import play.libs.Yaml;
import v1.post.PostData;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by ilya on 04.06.2017.
 */
public class InitialDataProvider implements InitialData {
  private final JPAApi jpaApi;
  private final play.Environment environment;
  @Inject
  public InitialDataProvider(JPAApi jpaApi, Environment environment) {
    this.environment=environment;
    this.jpaApi = jpaApi;
              int i=0;
             if (this.environment.isDev()) {
               wrap(em -> {
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
                 }
                 return null;
               });
             }
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
