package main

import (
	"net/http"
	"github.com/gin-gonic/gin"
	"strconv"
)

// album represents data about a record album.
type album struct {
	// ID     string  `json:"id"`
	Artist string  `json:"artist"`
	Title  string  `json:"title"`
	Year 	 string  `json:"year"`
}


// // getAlbums responds with the list of all albums as JSON.
// func getAlbums(c *gin.Context) {
// 	c.IndentedJSON(http.StatusOK, albums)
// }

func postAlbums(c *gin.Context) {

	err := c.Request.ParseMultipartForm(10 << 20)
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Unable to parse form data"})
	}

	var newAlbum album
	if err := c.ShouldBind(&newAlbum); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid data"})
		return
	}

	// albums = append(albums, newAlbum)

	file, fileHeader, err := c.Request.FormFile("image")
	if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "Unable to get file from form"})
			return
	}

	defer file.Close()
	imageSize := fileHeader.Size
	
	// Add the new album to the slice.
	// albums = append(albums, newAlbum)

	c.IndentedJSON(http.StatusCreated, gin.H{"albumID": 1, "imageSize": imageSize})
}

// getAlbumByID locates the album whose ID value matches the id
// parameter sent by the client, then returns that album as a response.
func getAlbumByID(c *gin.Context) {
	idStr := c.Param("id")

	id, err := strconv.Atoi(idStr)

	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalidID"})
		return
	}

	if id <= 0 {
		c.JSON(http.StatusBadRequest, gin.H{"error": "ID must be positive number"})
		return
	}

	singleAlbum := album{
		Artist: "The Sex Pistols",
		Title: "Never Mind The Bollocks!",
		Year: "1977",
	
	}
	

	c.IndentedJSON(http.StatusOK, singleAlbum)
}



func main() {
	router := gin.Default()
	router.GET("/albums/:id", getAlbumByID)
	// router.GET("/albums", getAlbums)
	router.POST("/albums", postAlbums)

	router.Run(":8080")
}
