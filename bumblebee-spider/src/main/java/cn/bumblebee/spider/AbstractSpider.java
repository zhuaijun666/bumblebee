package cn.bumblebee.spider;

import cn.bumblebee.spider.commens.TaskSpider;
import cn.bumblebee.spider.config.ClientConfig;
import cn.bumblebee.spider.processer.Processor;
import cn.bumblebee.spider.utils.Webutils;
import org.apache.http.client.methods.HttpRequestBase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 抽象蜘蛛
 * @param <T>
 */
public abstract class AbstractSpider<T extends HttpRequestBase, R>{

    private TaskSpider<T, R> taskSpider;
    private String charSet = Webutils.BASE_CHARSET;

    public AbstractSpider(String charSet) {
        this.charSet = charSet;
        taskSpider = new TaskSpider<>(this.getRequest(), this.charSet,
                this.getProcessor());
        ClientConfig clientConfig = getClientConfig();
        if (clientConfig != null) {
            taskSpider.setClientConfig(clientConfig);
        }
    }

    /**
     * 设置ClientConfig，包括COOKIE等内容
     * @return
     */
    public abstract ClientConfig getClientConfig();

    /**
     * 获取查询的Request
     * @return
     */
    public abstract T getRequest();

    /**
     * 处理过程
     * @return
     */
    public abstract Processor<String, R> getProcessor();

    /**
     * 启动爬取
     * @return
     */
    public R run() {
        ExecutorService exec = Executors.newFixedThreadPool(1);
        FutureTask<R> futureTask =
                new FutureTask<>(taskSpider);
        exec.submit(futureTask);
        R r = null;
        try {
            r = futureTask.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        exec.shutdown();
        return r;
    }

}
