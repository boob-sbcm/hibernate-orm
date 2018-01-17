/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or http://www.gnu.org/licenses/lgpl-2.1.html
 */
package org.hibernate.test.cdi.general.nonregistrymanaged;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.resource.beans.container.internal.ContainerManagedLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.container.spi.ExtendedBeanManager;
import org.hibernate.resource.beans.internal.FallbackBeanInstanceProducer;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import org.hamcrest.CoreMatchers;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Simulates a Hibernate ORM integrator consuming beans whose lifecycle is not managed by the registry,
 * but by the CDI engine only.
 *
 * @author Yoann Rodiere
 */
public class NonRegistryManagedBeanConsumingIntegrator implements Integrator {

	private ContainedBeanImplementor<TheApplicationScopedBean> applicationScopedBean1;
	private ContainedBeanImplementor<TheApplicationScopedBean> applicationScopedBean2;
	private ContainedBeanImplementor<TheDependentBean> dependentBean1;
	private ContainedBeanImplementor<TheDependentBean> dependentBean2;
	private ContainedBeanImplementor<TheNamedApplicationScopedBean> namedApplicationScopedBean1;
	private ContainedBeanImplementor<TheNamedApplicationScopedBean> namedApplicationScopedBean2;
	private ContainedBeanImplementor<TheNamedDependentBean> namedDependentBean1;
	private ContainedBeanImplementor<TheNamedDependentBean> namedDependentBean2;

	@Override
	@SuppressWarnings("unchecked")
	public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		ManagedBeanRegistry registry = sessionFactory.getServiceRegistry().getService( ManagedBeanRegistry.class );

		BeanContainer beanContainer = registry.getBeanContainer();
		assertThat( beanContainer, CoreMatchers.notNullValue() );


		applicationScopedBean1 = (ContainedBeanImplementor) beanContainer.getBean(
				TheApplicationScopedBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
		applicationScopedBean2 = (ContainedBeanImplementor) beanContainer.getBean(
				TheApplicationScopedBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
		dependentBean1 = (ContainedBeanImplementor) beanContainer.getBean(
				TheDependentBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
		dependentBean2 = (ContainedBeanImplementor) beanContainer.getBean(
				TheDependentBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
		namedApplicationScopedBean1 = (ContainedBeanImplementor) beanContainer.getBean(
				TheMainNamedApplicationScopedBeanImpl.NAME,
				TheNamedApplicationScopedBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
		namedApplicationScopedBean2 = (ContainedBeanImplementor) beanContainer.getBean(
				TheMainNamedApplicationScopedBeanImpl.NAME,
				TheNamedApplicationScopedBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
		namedDependentBean1 = (ContainedBeanImplementor) beanContainer.getBean(
				TheMainNamedDependentBeanImpl.NAME,
				TheNamedDependentBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
		namedDependentBean2 = (ContainedBeanImplementor) beanContainer.getBean(
				TheMainNamedDependentBeanImpl.NAME,
				TheNamedDependentBean.class,
				ContainerManagedLifecycleStrategy.INSTANCE,
				FallbackBeanInstanceProducer.INSTANCE
		);
	}

	/**
	 * Use one instance from each ManagedBean, ensuring that any lazy initialization is executed,
	 * be it in Hibernate ORM ({@link ExtendedBeanManager support})
	 * or in CDI (proxies).
	 */
	public void ensureInstancesInitialized() {
		applicationScopedBean1.getBeanInstance().ensureInitialized();
		applicationScopedBean2.getBeanInstance().ensureInitialized();
		dependentBean1.getBeanInstance().ensureInitialized();
		dependentBean2.getBeanInstance().ensureInitialized();
		namedApplicationScopedBean1.getBeanInstance().ensureInitialized();
		namedApplicationScopedBean2.getBeanInstance().ensureInitialized();
		namedDependentBean1.getBeanInstance().ensureInitialized();
		namedDependentBean2.getBeanInstance().ensureInitialized();
	}

	@Override
	public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
		applicationScopedBean1.release();
		applicationScopedBean2.release();
		dependentBean1.release();
		dependentBean2.release();
		namedApplicationScopedBean1.release();
		namedApplicationScopedBean2.release();
		namedDependentBean1.release();
		namedDependentBean2.release();
	}
}