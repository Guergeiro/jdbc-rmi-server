package databases;

public class Database {
  // Attributes
  private String URL;
  private String USER;
  private String PASS;
  
  // Constructor
  public Database(String URL, String USER, String PASS) {
    this.URL = URL + "/DS";
    this.USER = USER;
    this.PASS = PASS;
  }

  // Get & Set
  public String getURL() {
    return URL;
  }

  public void setURL(String uRL) {
    URL = uRL;
  }

  public String getUSER() {
    return USER;
  }

  public void setUSER(String uSER) {
    USER = uSER;
  }

  public String getPASS() {
    return PASS;
  }

  public void setPASS(String pASS) {
    PASS = pASS;
  }
}
