SELECT users.userId AS userId, city, users.stateId AS stateId, stateName, userConnections.id as connectionId, user1Id, user2Id
FROM users
    JOIN userStates ON userStates.stateId = users.stateId
    JOIN userConnections ON userConnections.id = users.connectionId
    WHERE users.userId = 777
