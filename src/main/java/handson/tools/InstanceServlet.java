package handson.tools;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.spanner.Instance;
import com.google.cloud.spanner.InstanceAdminClient;
import com.google.cloud.spanner.Options;
import com.google.cloud.spanner.Spanner;
import com.google.cloud.spanner.SpannerException;
import com.google.cloud.spanner.SpannerOptions;

import settings.SpannerSetting;

@SuppressWarnings("serial")
public class InstanceServlet extends HttpServlet {
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    SpannerOptions.Builder builder = SpannerOptions.newBuilder();
    builder.setCredentials(GoogleCredentials.fromStream(new FileInputStream(SpannerSetting.CREDENTIAL_PATH)));
    SpannerOptions options = builder.build();
    Spanner spanner = options.getService();

    try {
    	  InstanceAdminClient instanceAdmin = spanner.getInstanceAdminClient();
    	  Page<Instance> page = instanceAdmin.listInstances(Options.pageSize(1));
    	  while(page.hasNextPage()) {
    		  Iterable<Instance> values = page.getNextPage().getValues();
    		  for(Instance instance : values) {
    			  resp.getWriter().println("instance:" + instance.getDisplayName());
    		  }
    	  }
    } catch(SpannerException e) {
      resp.getWriter().println("exception error occurred. [detail]:" + e);
    } finally {
    	  spanner.close();
    }
    resp.getWriter().println("Instance Servlet.");
  }

}
