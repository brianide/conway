package ws.temple.conway.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ws.temple.util.Utils;

public class Rulestring implements Rules {
	
	private static final Pattern RULESTRING_PATTERN = Pattern.compile("^(?:b(\\d*)/?s(\\d*)|(\\d*)/(\\d*))$", Pattern.CASE_INSENSITIVE);

	private final int[] birth;
	private final int[] survival;
	
	public Rulestring(String rulestring) {
		final Matcher mat = RULESTRING_PATTERN.matcher(rulestring);
		if(mat.matches()) {
			final String births = Utils.firstNonNull(mat.group(1), mat.group(4));
			final String survivals = Utils.firstNonNull(mat.group(2), mat.group(3));
			birth = getDigitArray(births);
			survival = getDigitArray(survivals);
		}
		else {
			throw new IllegalArgumentException("Invalid rulestring");
		}
	}
	
	/**
	 * Converts a string of digits to an array of the digits as integers.
	 * 
	 * @param str
	 * @return
	 */
	private static int[] getDigitArray(String str) {
		// TODO Throw out repeated or otherwise invalid digits (with a warning?)
		final int[] digits = new int[str.length()];
		for(int i = 0; i < digits.length; i++) {
			digits[i] = Character.getNumericValue(str.charAt(i));
		}
		return digits;
	}
	
	@Override
	public boolean checkBirth(int neighbors) {
		for(int i = 0; i < birth.length; i++)
			if(birth[i] == neighbors)
				return true;
		return false;
	}

	@Override
	public boolean checkSurvival(int neighbors) {
		for(int i = 0; i < survival.length; i++)
			if(survival[i] == neighbors)
				return true;
		return false;
	}

}
