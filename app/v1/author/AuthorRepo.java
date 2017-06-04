package v1.author;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

/**
 * Created by ilya on 04.06.2017.
 */
public interface AuthorRepo {
  CompletionStage<Stream<Author>> listAuthors();

  CompletionStage<Author> create(Author postData);

  CompletionStage<Optional<Author>> get(Long id);

}
