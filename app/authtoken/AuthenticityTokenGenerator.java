package authtoken;

import org.abstractj.kalium.encoders.Encoder;
import org.abstractj.kalium.keys.AuthenticationKey;
import play.api.libs.Crypto;
import play.cache.CacheApi;
import play.cache.NamedCache;
import play.mvc.Http.Context;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class AuthenticityTokenGenerator {

  static CacheApi tokenSeedCache;

  static final int sessionTimeoutSec=1800;
	/**
	 * Generates a UUID and stores its signature in the session, used by the authenticity token
	 * @return
	 */


	public static String generate() {
    String atoken = Context.current().session().get(AuthTokenConstants.AUTH_TOKEN);
    String aseed;

    if (atoken!=null&&(aseed = tokenSeedCache.get(atoken))!=null)
      return aseed;

		byte[] b = new byte[32];
		new Random().nextBytes(b);

		AuthenticationKey aKey = new AuthenticationKey(b);
		String sign=aKey.toString();
		Context.current().session().put(AuthTokenConstants.AUTH_TOKEN, sign);
    tokenSeedCache.set(sign,aseed=Arrays.toString(b),sessionTimeoutSec);
		return aseed;
	}
	public static void removeSeedFromCache(String token){
	   tokenSeedCache.remove(token);
  }
  public static void setTokenSeedCache(CacheApi tokenSeedCache) {
	  if (AuthenticityTokenGenerator.tokenSeedCache==null)
      AuthenticityTokenGenerator.tokenSeedCache = tokenSeedCache;
  }
}
