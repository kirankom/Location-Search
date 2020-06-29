# Location Search

Grab, store, and compress user data in an efficient way

# Data Utilities

- Store and compress timestamps into roaring bitmaps by making them relative to the first timestamp
- Encode and Decode the lat/long values using the given Polyline algorithm
- Serialize the bitmap into a byte array, and Deserialize the byte array into a bitmap

# Database Writer

- Parse the JSON file into latitude, longitude, and timestamp
  - Need to come up with a user ID for each user, or will that be given?
- Store the output in a MySQL Database
- 4 columns: userID, first timestamp, byte[] timestamps, byte[] lat/long

# Data Generation

- Grab your data from Google Maps. Multiply/replicate the data so it reflects the data of say 1000 users
- 1000 users \* 1000 location events per user per day = 1 million data points

# Storing Data

- Store the data in Polygons Look into Bitmaps and Roaring Bitmaps

# Compression

- Use Roaring Bitmaps to compress the Polygon of data and then store it in vector form. The compressed data should be as close to real life as possible

# Timeline:

- Due in 8 - 12 Weeks (around July 20th - August 17th)

1. compression for bitmap timestamps
2. decouple database writing with instantiation - initialize compression  
   and encoder instances in constructor
3. remove all the static methods
4. later - database reader
   - get a subset of the requested data for the given user id and a time range
5. change varbinary byte size in database to longBlob
6. make list into iterable

Note: Make sure to call establish connection in test method
