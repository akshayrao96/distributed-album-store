import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@MultipartConfig
@WebServlet(name = "AlbumsServlet", value = "/albums/*")
public class AlbumsServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("application/json");
    Gson gson = new Gson();

    String urlPath = request.getPathInfo();

    Albums example = new Albums("Sex Pistols", "Never Mind The Bollocks!", "1977");

    if (urlPath == null || urlPath.isEmpty()) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      response.getWriter().write("missing url parameters!");
      return;
    }

    String[] urlParts = urlPath.split("/");
    if (!isUrlValidGet(urlParts)) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      response.getOutputStream().print(gson.toJson(example));
      response.setStatus(HttpServletResponse.SC_OK);
    }
  }

  private boolean isUrlValidGet(String[] urlPath) {
    return urlPath.length == 2 && isValidID(urlPath[1]);
  }

  private boolean isValidID(String s) {
    try {
      int id = Integer.parseInt(s);
      return id > 0;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("application/json");

    String urlPath = request.getPathInfo();

    if (urlPath != null) {
      String[] urlParts = urlPath.split("/");
      if (!isUrlValidPost(urlParts)) {
        response.getWriter().write("Invalid parameters given\n");
        response.getWriter().write("usage: /albums");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
      }

    } else {

      Gson gson = new Gson();
      String data = request.getParameter("profile");

//      Albums album = gson.fromJson(data, Albums.class);
//      JsonObject jsonObject = new Gson().toJsonTree(album).getAsJsonObject();


//      String artist = jsonObject.get("artist").getAsString();
//      String title = jsonObject.get("title").getAsString();
//      String year = jsonObject.get("year").getAsString();
//      Albums album = new Albums(artist, title, year);

      String imgSize = "0 KB";

      Part part = request.getPart("image");

      if (part != null) {
        imgSize = part.getSize() + " KB";
      } else {
        response.getOutputStream().print("null");
      }

      Map<String, String> responseMap = new HashMap<>();
      responseMap.put("albumID", "1");
      responseMap.put("imageSize", imgSize);

      response.getOutputStream().print(gson.toJson(responseMap));

    }
  }

  private boolean isUrlValidPost(String[] urlPath) {
    return urlPath == null || urlPath.length == 0;
  }
}

