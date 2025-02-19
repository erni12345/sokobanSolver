as we are solving the game, keep a hashmap of dead squares 


in dead square detection, 
consider boxes to be transparent. 


## Things to do

 - implement dead square detection
 - think of a good heuristic thats fast to calculate / does not have to be recalculated very frequently
 - board representation


## Dead square detection pseudocode

initialize board with a box at a goal (gx, gy)
set current position to goal position (cx, cy) = (gx, gy)
ds_dfs
    for pos in ((-1,0), (1,0), (0,-1), (0,1)) 
        if possible, place player at position (2*pos.x + cx, 2*pos.y + cy) # possible - not a wall
        else continue
        if possible, place box at position (pos.x + cx, pos.y + cy)
        else continue
        
        mark current as visited
        
        make recursive ds_dfs call where we undo into the position
        
