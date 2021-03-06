package moar.sugar;

public class UsPostalAddress {
  private static boolean isTwoWordState(String state) {
    String[] stateWords = { "Carolina", "Dakota", "Hampshire", "Island", "Jersey", "Mexico", "Virginia", "York", };
    for (String word : stateWords) {
      if (state.equalsIgnoreCase(word)) {
        return true;
      }
    }
    return false;
  }
  public String zip;
  public String line1;
  public String line2;
  public String city;

  public String state;

  public UsPostalAddress(String singleLineAddress) {
    singleLineAddress = singleLineAddress.replaceAll(", USA$", "");
    singleLineAddress.replace('.', ',');
    singleLineAddress.replace(",,", ",");
    UsPostalAddress address = this;
    String s = singleLineAddress;
    String endsWithZip = "[0-9]{5}(?:-[0-9]{4})?$";
    if (s.matches("^" + endsWithZip)) {
      address.zip = s;
      return;
    }

    // Parse line 1
    if (s.matches("^[0-9]* .*")) {
      address.line1 = s.replaceAll(",.*", "").trim();
      s = s.substring(address.line1.length() + 1).trim();
      if (s.isEmpty()) {
        return;
      }
    }

    String endsWithStateCode = "\\s*(?i)[A-Z]{2}$";
    String endsWithStateCodePlusZip = "\\s*(?i)[A-Z]{2}\\s*" + endsWithZip;
    if (s.matches(".*" + endsWithStateCode)) {
      // Parse city with 2 digit state code
      address.city = s.replaceAll(endsWithStateCode, "").replaceAll(",", "").trim();
    } else if (s.matches(".*" + endsWithStateCodePlusZip)) {
      // Parse city with 2 digit state code plus zip
      address.city = s.replaceAll(endsWithStateCodePlusZip, "").replaceAll(",", "").trim();
    } else if (s.matches(".*" + endsWithZip)) {
      // Parse zip by assuming it
      address.zip = s.replaceAll(".* ", "").trim();
      s = s.substring(0, s.length() - address.zip.length());
      address.state = MoarStringUtil.lastWord(s);
      s = s.substring(0, s.length() - address.state.length() - 1);
      if (isTwoWordState(address.state)) {
        String stateWord = MoarStringUtil.lastWord(s);
        if (address.state.equalsIgnoreCase("(?i)Virginia")) {
          if (stateWord.equalsIgnoreCase("(?i)West")) {
            s = s.substring(0, s.length() - stateWord.length());
            address.state = stateWord + " " + address.state;
          }
        } else {
          s = s.substring(0, s.length() - stateWord.length());
          address.state = stateWord + " " + address.state;
        }
      }
      address.city = s.replaceAll(",", "").trim();
      return;
    }
    s = s.replaceAll(",", "");
    s = s.substring(address.city == null ? 0 : address.city.length());
    s = s.trim();
    if (s.isEmpty()) {
      return;
    }

    s = s.replaceAll("\\s.*", "");

    if (!s.matches("\\d*")) {
      // Parse state
      address.state = s;
      s = s.substring(address.state == null ? 0 : address.state.length()).trim();
      if (s.isEmpty()) {
        return;
      }
    }

    // Parse zip
    address.zip = s;
  }

}
