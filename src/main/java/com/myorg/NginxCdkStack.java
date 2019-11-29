package com.myorg;

import java.util.Collections;
import java.util.List;

import software.amazon.awscdk.core.Construct;
import software.amazon.awscdk.core.Stack;
import software.amazon.awscdk.core.StackProps;
import software.amazon.awscdk.services.autoscaling.AutoScalingGroup;
import software.amazon.awscdk.services.ec2.AmazonLinuxImage;
import software.amazon.awscdk.services.ec2.Connections;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Port;
import software.amazon.awscdk.services.ec2.SubnetSelection;
import software.amazon.awscdk.services.ec2.SubnetType;
import software.amazon.awscdk.services.ec2.UserData;
import software.amazon.awscdk.services.ec2.Vpc;

public class NginxCdkStack extends Stack {
	public NginxCdkStack(final Construct scope, final String id) {
		this(scope, id, null);
	}

	public NginxCdkStack(final Construct scope, final String id, final StackProps props) {
		super(scope, id, props);

		Vpc vpc = Vpc.Builder.create(this, "VPC-nginx").enableDnsHostnames(true).enableDnsSupport(true).cidr("172.31.235.0/24").build();
		
		MyAutoScalingGroupProps autoScalingGroupProps = new MyAutoScalingGroupProps();
		autoScalingGroupProps.vpc = vpc;
		
		new MyAutoScalingroup(this, "MyAutoScalingroup", autoScalingGroupProps);

	}

	static class MyAutoScalingGroupProps {
		public Vpc vpc;
	}

	static class MyAutoScalingroup extends Construct {
		MyAutoScalingroup(final Construct parent, final String name, final MyAutoScalingGroupProps props) {
			super(parent, name);
			
			UserData userData = UserData.forLinux();
			userData.addCommands("yum install -y nginx","echo \"Test Ngnix!\" >> /var/www/html/index.html", "chkconfig nginx on", "service nginx start");
			
			Connections.Builder.create().defaultPort(Port.tcp(80));
			SubnetSelection.builder().subnetType(SubnetType.PUBLIC);
			AutoScalingGroup.Builder.create(this, "Nginx").instanceType(new InstanceType("t2.micro")).keyName("aws-cdk").allowAllOutbound(false)
					.machineImage(new AmazonLinuxImage()).associatePublicIpAddress(false).userData(userData).vpc(props.vpc).build();
		}
	}

	@Override
	public List<String> validate() {
		System.err.println("Validating MyAutoScalingGroup...");
		return Collections.emptyList();
	}
}
