package authtoken.validator;

import javax.validation.ConstraintValidator;

import authtoken.AuthenticityTokenGenerator;
import org.abstractj.kalium.encoders.Encoder;
import org.abstractj.kalium.keys.AuthenticationKey;
import play.api.libs.Crypto;
import play.data.validation.Constraints.Validator;
import play.libs.F.*;
import static play.libs.F.*;
import play.mvc.Http.Session;
import authtoken.AuthTokenConstants;

/**
 * This class defined a new Play validator
 * 
 * @author orefalo
 */
public class AuthenticityTokenValidator extends Validator<Object> implements
		ConstraintValidator<AuthenticityToken, Object> {

	/* Default error message */
	final static public String message = "error.browserid";

	/**
	 * Validator init Can be used to initialize the validation based on
	 * parameters passed to the annotation
	 */
	@Override
	public void initialize(AuthenticityToken constraintAnnotation) {
	}

	/**
	 * The validation itself
	 */
	@Override
	public boolean isValid(Object uuid) {
		Session session = play.mvc.Http.Context.current().session();
		String atoken = session.get(AuthTokenConstants.AUTH_TOKEN);
		session.remove(AuthTokenConstants.AUTH_TOKEN);
    AuthenticityTokenGenerator.removeSeedFromCache(atoken);
		
		if (atoken == null || uuid == null)
			return false;
    String uuidStr = ((String)uuid).replace("[","").replace("]","");
		String[] uBytesStr= uuidStr.split(",");
		byte[] uBytes = new byte[uBytesStr.length];
		for (int i=0;i<uBytesStr.length;i++)
      uBytes[i]=Byte.parseByte(uBytesStr[i].trim());
		String sign = new AuthenticationKey(uBytes).toString();
		return atoken.equals(sign);
	}

	/**
	 * Constructs a validator instance.
	 */
	public static play.data.validation.Constraints.Validator<Object> authenticationToken() {
		return new AuthenticityTokenValidator();
	}

	@Override
    public Tuple<String, Object[]> getErrorMessageKey() {
        return Tuple(message, new Object[] {});
    }

    public static boolean authenticationTokenIsValid(){
        String[] authtokens = play.mvc.Http.Context.current().request().body().asFormUrlEncoded().get("authtoken");
        if (authtokens == null || authtokens.length==0) return false;
        String authtoken = authtokens[0];
        return AuthenticityTokenValidator.authenticationToken().isValid(authtoken);
    }
}