SELECT DISTINCT *
FROM (
    SELECT *
    FROM userConnections
    WHERE user1Id = <userId> OR user2Id = <userId>
) AS s
LEFT JOIN users ON users.userId = <userId>
