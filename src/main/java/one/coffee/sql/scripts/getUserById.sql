SELECT user1Id, city1, state1Id, connection1Id, user2Id, users.city AS city2, users.stateId AS state2Id, userConnections.id AS connection2Id
FROM (
    SELECT users.userId AS user1Id, users.city AS city, users.stateId AS state1Id, userConnections.id AS connection1Id, user2Id
    FROM users
        LEFT JOIN userConnections ON userConnections.id = users.connectionId
        WHERE users.userId = <user1Id>
) AS first_part
LEFT JOIN users ON users.id = user2Id
