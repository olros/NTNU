from __future__ import annotations

from Map import Map_Obj


class Node:
    def __init__(self, position: tuple[int, int], best_parent: Node = None):
        self.position: tuple[int, int] = position
        self.children: list[Node] = []
        self.best_parent: Node | None = best_parent

        self.g = 0
        """ Cost of getting to this node """
        self.h = 0
        """ Estimated cost to goal """

    def f(self):
        """Estimated total cost of a solution path going through this node; f = g + h"""
        return self.g + self.h

    def __eq__(self, other: Node):
        """Overload to allow comparing two nodes by their position"""
        return self.position == other.position

    def __gt__(self, other: Node):
        """Overload `gt` to allow sorting the list based on f-value"""
        return self.f() > other.f()


class AStar:
    def __init__(self, map_obj: Map_Obj, start_pos: tuple[int, int], end_pos: tuple[int, int]):
        self.closed_nodes: list[Node] = []
        """List of visited nodes"""
        self.open_nodes: list[Node] = []
        """List of not yet visited nodes"""
        self.map_obj = map_obj

        self.end_node = Node(position=end_pos)
        """The goal node"""

        start_node = Node(position=start_pos)
        start_node.h = self._calc_manhattan_distance(start_node.position)
        self.open_nodes.append(start_node)

    def run(self):
        while self.open_nodes:
            current_node = self.open_nodes.pop(0)
            self.closed_nodes.append(current_node)

            # Found the goal, traversing from the goal back to the start to return the path as list of positions
            if current_node == self.end_node:
                path: list[tuple[int, int]] = []
                curr_node = current_node
                while curr_node is not None:
                    path.append(curr_node.position)
                    curr_node = curr_node.best_parent
                # Return the path in reverse so that it can be followed from the start
                return path[::-1]

            new_children: list[Node] = []
            for new_position in [(0, -1), (0, 1), (-1, 0), (1, 0)]:
                node_position = (
                    current_node.position[0] + new_position[0], current_node.position[1] + new_position[1])

                # If the value is `-1` there is a wall at the position and we can therefore not move there
                if self.map_obj[node_position[0]][node_position[1]] == -1:
                    continue

                new_node = Node(position=node_position,
                                best_parent=current_node)
                new_children.append(new_node)

            for child in new_children:
                # The tile cost is the g cost of the current child, which is found on the map
                tile_cost: int = self.map_obj[child.position[0]
                                              ][child.position[1]]

                # If the node already exists in either the open or closed nodes, use and update the existing node instead
                for i in range(len(self.closed_nodes)):
                    if self.closed_nodes[i] == child:
                        child = self.closed_nodes[i]
                for i in range(len(self.open_nodes)):
                    if self.open_nodes[i] == child:
                        child = self.open_nodes[i]

                current_node.children.append(child)

                if child not in self.open_nodes and child not in self.closed_nodes:
                    self._attach_and_eval(child, current_node, tile_cost)
                    self.open_nodes.append(child)
                    self.open_nodes.sort()
                elif current_node.g + tile_cost < child.g:
                    self._attach_and_eval(child, current_node, tile_cost)
                    if child in self.closed_nodes:
                        self._propagate_path_improvements(child)

        # A path could not be found between start and end positions
        return False

    def _calc_manhattan_distance(self, start: tuple[int, int]):
        """Calculates a hueristic distance between the start and end position"""
        return abs(start[0] - self.end_node.position[0]) + abs(start[1] - self.end_node.position[1])

    def _attach_and_eval(self, child: Node, parent: Node, tile_cost: int):
        """Attached the node to its parent and calculates g and h"""
        child.best_parent = parent
        child.g = parent.g + tile_cost
        child.h = self._calc_manhattan_distance(child.position)

    def _propagate_path_improvements(self, parent: Node):
        """Send possible path improvements of a node to its children recursively"""
        for child in parent.children:
            if parent.g + 1 < child.g:
                child.best_parent = parent
                child.g = parent.g + 1
                self._propagate_path_improvements(child)


def draw_movement_path(map_obj: Map_Obj, path: list[tuple[int, int]]):
    """
    Update the cell value of each of the path-positions to create a yellow path when visualized
    """
    for position in path:
        map_obj.set_cell_value(position, " Ø ", True)


def main():
    task = 0
    while (task < 1 or task > 4):
        task = int(input("Select task to run: (1, 2, 3 or 4) "))

    samfundet = Map_Obj(task)
    map1, _ = samfundet.get_maps()

    tuple_start = (samfundet.get_start_pos()[0], samfundet.get_start_pos()[1])
    tuple_end = (samfundet.get_goal_pos()[0], samfundet.get_goal_pos()[1])

    path = AStar(map1, tuple_start, tuple_end).run()

    try:
        draw_movement_path(samfundet, path)
        print("Found a path from start to goal!")
    except TypeError:
        print("Could not find a path from start to goal ):")

    samfundet.show_map()


if __name__ == '__main__':
    main()
