#include "../Server.hpp"
#include <string>
#include <unistd.h>
#include <cassert>

const std::string FINISH_FIRST = "Should finish first";
const std::string FINISH_LAST = "Should finish be last";

void test_postAfter_waits_before_running_function() {
    Workers event_loop(1);
    std::vector <std::string> order_of_completion;

    event_loop.start();
    event_loop.post_after([&order_of_completion] {
        order_of_completion.emplace_back(FINISH_LAST);
    }, [] {
        sleep(1);
    });

    event_loop.post([&order_of_completion] {
        order_of_completion.emplace_back(FINISH_FIRST);
    });

    event_loop.stop();
    event_loop.join();
    assert(order_of_completion[0] == FINISH_FIRST && order_of_completion[1] == FINISH_LAST &&
           order_of_completion.size() == 2);
}

void test_Post_tasks_are_completed_in_right_order() {
    Workers event_loop(1);
    std::vector<int> order_of_completion;
    for (int i = 0; i < 1000; ++i) {
        //Have to pass copy of i
        event_loop.post([&order_of_completion, i] {
            order_of_completion.emplace_back(i);
        });
    }

    event_loop.start();
    event_loop.stop();
    event_loop.join();

    for (int i = 0; i < 1000; ++i) {
        assert(order_of_completion[i] == i);
    }
}

class WorkersTests {
public:
    int test();
};

int WorkersTests::test() {
    test_postAfter_waits_before_running_function();
    test_Post_tasks_are_completed_in_right_order();
    std::cout << "All WorkersTests passed" << std::endl;
    return 0;
}

