SELECT DISTINCT *
FROM (
    SELECT *
    FROM users
    WHERE userId = <userId>
) AS s
    LEFT JOIN userConnections ON userConnections.user1Id = <userId> OR userConnections.user2Id = <userId>

SELECT s2.userId AS userId, s2.city AS city, s2.stateId AS stateId, s2.connectionId AS connectionId
FROM (
         SELECT DISTINCT userId, city, stateId, connectionId, user2Id
         FROM (
                  SELECT *
                  FROM users
                  WHERE userId = 123
              ) AS s1
                  LEFT JOIN userConnections ON userConnections.user1Id = 123 OR userConnections.user2Id = 123
     ) AS s2
         LEFT JOIN users ON users.userId = s2.user2Id
