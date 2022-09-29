#include <iostream>
#include <condition_variable>
#include <thread>
#include <functional>
#include <list>
#include <vector>
#include <atomic>

using namespace std;

class Workers
{
  vector<thread> thread_list;
  mutex tasks_mutex;
  list<function<void()>> tasks;
  int no_of_threads;
  condition_variable cv;
  atomic<bool> run{true};

public:
  Workers(int thread_amount)
  {
    no_of_threads = thread_amount;
  };

  void start()
  {
    run = true;
    for (int i = 0; i < no_of_threads; i++)
    {
      thread_list.emplace_back([&] {
        while (true)
        {
          function<void()> task;
          {
            unique_lock<mutex> lock(tasks_mutex);
            while (tasks.empty())
            {
              if (!run)
              {
                return;
              }
              cv.wait(lock);
            }
            task = *tasks.begin();
            tasks.pop_front();
          }
          if (task)
            task();
        }
      });
    }
  }

  void post(function<void()> func)
  {
    tasks.emplace_back(func);
    cv.notify_one();
  }

  void post_timeout(function<void()> func, int ms)
  {
    tasks.emplace_back([ms, func] {
      this_thread::sleep_for(chrono::milliseconds(ms));
      func();
    });
  }

  void join()
  {
    for (auto &thread : thread_list)
    {
      thread.join();
    }
  }

  void stop()
  {
    run.exchange(false);
    cv.notify_all();
  }
};

int main()
{
  {
    Workers worker_threads(4);
    Workers event_loop(1);

    worker_threads.start(); // Create 4 internal threads
    event_loop.start();     // Create 1 internal thread

    for (int i = 0; i < 5; i++)
    {
      worker_threads.post([i, &event_loop] {
        this_thread::sleep_for(2s);

        event_loop.post([i] {
          cout << "Result: " << i << ';' << endl;
        });
      });
    }

    worker_threads.post_timeout([] {
      cout << "delayed" << endl;
    }, 3000);

    this_thread::sleep_for(chrono::seconds(5));

    worker_threads.stop();
    event_loop.stop();

    worker_threads.join(); // Calls join() on the worker threads
    event_loop.join();
  }
}