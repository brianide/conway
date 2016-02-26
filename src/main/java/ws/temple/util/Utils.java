package ws.temple.util;

public class Utils {
	
	@SafeVarargs
	public static <T> T firstNonNull(T... objects) {
		for(T obj : objects)
			if(obj != null)
				return obj;
		return null;
	}

}
