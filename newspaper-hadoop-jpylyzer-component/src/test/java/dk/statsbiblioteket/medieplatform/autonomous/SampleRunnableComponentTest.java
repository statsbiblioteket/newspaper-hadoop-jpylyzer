package dk.statsbiblioteket.medieplatform.autonomous;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.Assert.assertTrue;

public class SampleRunnableComponentTest {


    @Test(groups = "integrationTest")
    public void testdoWorkOnItem() throws Exception {
        String pathToProperties = System.getProperty("integration.test.newspaper.properties");
        Properties properties = new Properties();
        properties.load(new FileInputStream(pathToProperties));

        Batch batch = new Batch("400022028241");

        properties.setProperty(
                ConfigConstants.JOB_FOLDER, "inputFiles-cibuild");
        properties.setProperty(
                ConfigConstants.PREFIX,
                "/net/zone1.isilon.sblokalnet/ifs/archive/bitmag-devel01-data/cache/avisbits/perm/avis/");
        properties.setProperty(ConfigConstants.HADOOP_USER, "newspapr");
        properties.setProperty(ConfigConstants.JPYLYZER_PATH, "/usr/lib/python2.7/site-packages/jpylyzer/jpylyzer.py");
        properties.setProperty(ConfigConstants.FILES_PER_MAP_TASK, "5");

        clean(properties.getProperty(ConfigConstants.JOB_FOLDER));

        JpylyzerRunnableComponent component = new JpylyzerRunnableComponent(properties);
        ResultCollector resultCollector = new ResultCollector("crap", "crap");

        component.doWorkOnItem(batch, resultCollector);
        assertTrue(resultCollector.isSuccess(), resultCollector.toReport());
    }

    private void clean(String jobFolder) throws IOException, InterruptedException {
        Configuration conf = new Configuration(true);
        String user = conf.get(ConfigConstants.HADOOP_USER, "newspapr");
        FileSystem fs = FileSystem.get(FileSystem.getDefaultUri(conf), conf, user);
        fs.delete(new Path(jobFolder), true);
    }

}
