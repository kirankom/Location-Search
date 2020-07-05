1. later - database reader
   - get a subset of the requested data for the given user id and a time range
2. change varbinary byte size in database to longBlob
3. make list into iterable
4. Abstract away error throwing
   - throw errors for invalid negative inputs, empty iterables, etc
5. Use Log4j instead of print statements
6. DONE - Test Compression methods

Three test cases:

1. series of timestamps and lat longs and data gets updated
2. Updates data with today's data
3. searching for timestamps with time bounds

maven shade plugin -- build UBER Jar

Note: Make sure to call establish connection in test method
