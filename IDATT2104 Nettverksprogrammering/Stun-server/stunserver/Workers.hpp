#include <iostream>
#include <list>
#include <functional>
#include <vector>
#include <thread>
#include <mutex>
#include <condition_variable>
#include <atomic>

class Workers
{
private:
    int n;
    std::list<std::function<void()>> tasks;
    std::mutex tasks_lock;
    std::vector<std::thread> threads;
    std::condition_variable cv;
    std::atomic<bool> keep_going;
    //Have to add this so that the stop method waits until tasks posted by post-timeout are able to complete aswell
    std::atomic<long> tasks_to_be_done;

public:
    Workers(int number_of_threads);
    void start();
    void post(std::function<void()> task);
    void post_after(std::function<void()> task, std::function<void()> to_be_done);
    void stop();
    void join();
    ~Workers();
};

Workers::Workers(int number_of_threads)
{
    n = number_of_threads;
}

void Workers::start()
{
    keep_going.operator=(true);
    for (int i = 0; i < n; ++i)
    {
        threads.emplace_back([this] {
            while (keep_going)
            {
                std::function<void()> task;
                {
                    std::unique_lock<std::mutex> lock(this->tasks_lock);
                    while (tasks.empty() && keep_going)
                        cv.wait(lock);
                    if (keep_going)
                    {
                        task = *tasks.begin();
                        tasks.pop_front();
                        tasks_to_be_done.operator--();
                    }
                }
                if (task)
                    task();
            }
        });
    }
}

void Workers::post(std::function<void()> task)
{
    tasks_to_be_done.operator++();
    std::unique_lock<std::mutex>(this->tasks_lock);
    tasks.emplace_back(task);
    cv.notify_one();
}

void Workers::post_after(std::function<void()> task, std::function<void()> to_be_done)
{
    tasks_to_be_done.operator++();
    //Not using mutex lock on threads vector since this method is only supposed to be called from the singlethreaded main method
    threads.emplace_back([this, task, to_be_done] {
        to_be_done();
        std::unique_lock<std::mutex>(this->tasks_lock);
        tasks.emplace_back(task);
        cv.notify_one();
    });
}

void Workers::stop()
{
    threads.emplace_back([this] {
        while (keep_going)
        {
            std::unique_lock<std::mutex>(this->tasks_lock);
            if (tasks.empty() && tasks_to_be_done == 0)
            {
                keep_going.operator=(false);
                cv.notify_all();
            }
            else
                cv.notify_one();
        }
    });
}

void Workers::join()
{
    //Using threads.size() since post_timeout adds a new thread to the vector
    for (int i = 0; i < threads.size(); ++i)
    {
        threads[i].join();
    }
}

Workers::~Workers() {
    std::unique_lock<std::mutex>(this->tasks_lock);
    tasks.clear();
    threads.clear();
}