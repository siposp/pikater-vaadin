-- varianta pro postgres
CREATE TABLE filemapping (userId INTEGER NOT NULL, externalFilename VARCHAR(256) NOT NULL, internalFilename VARCHAR(256) NOT NULL, PRIMARY KEY (userID, externalFilename));
CREATE TABLE results (userID INTEGER NOT NULL, agentName VARCHAR (256), agentType VARCHAR (256), options VARCHAR (256), dataFile VARCHAR (50), testFile VARCHAR (50), errorRate DOUBLE precision, kappaStatistic DOUBLE precision, meanAbsoluteError DOUBLE precision, rootMeanSquaredError DOUBLE precision, relativeAbsoluteError DOUBLE precision, rootRelativeSquaredError DOUBLE precision, start VARCHAR (50), finish VARCHAR (50), duration VARCHAR (50), durationLR VARCHAR (50), objectFilename VARCHAR (50), experimentID INTEGER NOT NULL, experimentName VARCHAR (50), note VARCHAR (50) );
CREATE TABLE metadata (externalFilename VARCHAR (256) NOT NULL, internalFilename VARCHAR (256) NOT NULL, defaultTask VARCHAR (256) DEFAULT NULL, attributeType VARCHAR (256) DEFAULT NULL, numberOfInstances INTEGER DEFAULT NULL, numberOfAttributes INTEGER DEFAULT NULL, missingValues BOOLEAN DEFAULT NULL, CONSTRAINT metadata_pkey PRIMARY KEY (internalfilename));
