package model;

import com.google.gson.Gson;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

public class DynamoDBController {

  private final DynamoDbClient ddb;
  private final Gson gson;
  private final String tableName;
  private SdkBytes image;

  public DynamoDBController(DynamoDbClient ddb, String tableName) throws IOException {
    this.ddb = ddb;
    this.gson = new Gson();
    this.tableName = tableName;
    generateImage();
  }

  private void generateImage() throws IOException {
    BufferedImage filler = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(filler, "png", baos);
    baos.flush();
    this.image = SdkBytes.fromByteArray(baos.toByteArray());
    baos.close();
  }

  public void postProfile(AlbumProfile album) {
    Map<String, AttributeValue> itemValues = new HashMap<>();
    itemValues.put("albumID", AttributeValue.builder().s(album.getAlbumID()).build());
    itemValues.put("profile", AttributeValue.builder().s(gson.toJson(album.getProfile())).build());
    itemValues.put("image", AttributeValue.builder().b(this.image).build());

    PutItemRequest request = PutItemRequest.builder()
        .tableName(tableName)
        .item(itemValues)
        .build();

    ddb.putItem(request);
  }

  public Album getProfile(String albumID) {
    Map<String, AttributeValue> keyMap = new HashMap<>();
    keyMap.put("albumID", AttributeValue.builder().s(albumID).build());

    GetItemRequest getItemRequest = GetItemRequest.builder()
        .key(keyMap)
        .tableName(tableName)
        .build();

    GetItemResponse response = ddb.getItem(getItemRequest);
    Map<String, AttributeValue> item = response.item();

    if (item != null && item.containsKey("profile")) {
      String albumDataJson = item.get("profile").s();
      return gson.fromJson(albumDataJson, Album.class);
    }
    return null;
  }
}
