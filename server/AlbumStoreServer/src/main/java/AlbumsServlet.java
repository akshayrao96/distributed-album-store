import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
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

    String[] urlParts = urlPath.split("/");
    if (urlParts.length != 2) {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } else {
      response.getOutputStream().print(gson.toJson(example));
      response.setStatus(HttpServletResponse.SC_OK);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    response.setContentType("application/json");
    StringBuilder imgSize = new StringBuilder();
    Part part = request.getPart("image");
    long size = part.getSize();
    imgSize.append(size).append("KB");

    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("albumID", "1");
    jsonObject.addProperty("imageSize", imgSize.toString());

    try (PrintWriter out = response.getWriter()) {
      out.write(jsonObject.toString());
    }
  }
}

