/*
 * Copyright 2011 Witoslaw Koczewsi <wi@koczewski.de>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package ilarkesto.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHasher {

	public static final String ALGORITHM_SHA_256 = "SHA-256";

	public static String hashPasswordWithSha256(String password, String salt, String prefix) {
		return hashPassword(ALGORITHM_SHA_256, password, salt, prefix);
	}

	public static String hashPassword(String algorithm, String password, String salt, String prefix) {
		if (salt == null) salt = "";
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Unsupported algorithm: " + algorithm, ex);
		}
		if (salt != null) password += salt;
		md.update(password.getBytes());

		byte byteData[] = md.digest();

		StringBuilder sb = new StringBuilder();
		if (prefix != null) sb.append(prefix);
		for (int i = 0; i < byteData.length; i++) {
			sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

	public static String hashPassword(String password, String salt) {
		return hashPasswordWithSha256(password, salt, null);
	}

}
