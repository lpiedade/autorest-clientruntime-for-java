/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.arm.dag;

import com.microsoft.azure.arm.model.Appliable;
import com.microsoft.azure.arm.model.Creatable;
import com.microsoft.azure.arm.model.Executable;
import com.microsoft.azure.arm.model.Indexable;
import com.microsoft.azure.arm.utils.SdkContext;
import rx.Completable;
import rx.Observable;
import rx.functions.Func1;

import java.util.Objects;

/**
 * An index-able TaskItem with a TaskGroup.
 */
public abstract class IndexableTaskItem
        implements Indexable, TaskItem, TaskGroup.HasTaskGroup {
    /**
     * The key that is unique to this TaskItem which is used to index this
     * TaskItem.
     */
    private final String key;
    /**
     * The TaskGroup with this TaskItem as root task.
     */
    private final TaskGroup taskGroup;
    /**
     * The result of computation performed by this TaskItem.
     */
    private Indexable taskResult;

    /**
     * Creates a TaskItem which is index-able using provided key.
     *
     * @param key the unique key to index this TaskItem
     */
    public IndexableTaskItem(String key) {
        this.key = key;
        this.taskGroup = new TaskGroup(this);
        this.taskResult = null;
    }

    /**
     * Creates a TaskItem which is index-able using a random UUID.
     */
    public IndexableTaskItem() {
        this(SdkContext.randomUuid());
    }

    /**
     * Creates an IndexableTaskItem from provided FunctionalTaskItem.
     *
     * @param taskItem functional TaskItem
     * @return IndexableTaskItem
     */
    public static IndexableTaskItem create(final FunctionalTaskItem taskItem) {
        return new IndexableTaskItem() {
            @Override
            protected Observable<Indexable> invokeTaskAsync(TaskGroup.InvocationContext context) {
                FunctionalTaskItem.Context fContext = new FunctionalTaskItem.Context(this);
                fContext.setInnerContext(context);
                return taskItem.call(fContext);
            }
        };
    }

    /**
     * @return the TaskGroup this this TaskItem as root.
     */
    @Override
    public TaskGroup taskGroup() {
        return this.taskGroup;
    }

    /**
     * Clear the result produced by the task.
     */
    public void clear() {
        this.taskResult = voidIndexable();
    }

    @Override
    public String key() {
        return this.key;
    }

    /**
     * Add a dependency task item for this task item.
     *
     * @param dependency the dependency task item.
     * @return key to be used as parameter to taskResult(string) method to retrieve result the task item
     */
    protected String addDependency(FunctionalTaskItem dependency) {
        Objects.requireNonNull(dependency);
        return this.taskGroup.addDependency(dependency);
    }

    /**
     * Add a dependency for this task item.
     *
     * @param dependency the dependency.
     * @return key to be used as parameter to taskResult(string) method to retrieve result of root
     * task in the given dependency task group
     */
    protected String addDependency(TaskGroup.HasTaskGroup dependency) {
        Objects.requireNonNull(dependency);
        this.taskGroup.addDependencyTaskGroup(dependency.taskGroup());
        return dependency.taskGroup().key();
    }

    /**
     * Add a creatable dependency for this task item.
     *
     * @param creatable the creatable dependency.
     * @return the key to be used as parameter to taskResult(string) method to retrieve created dependency
     */
    @SuppressWarnings("unchecked")
    protected String addDependency(Creatable<? extends Indexable> creatable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) creatable;
        return this.addDependency(dependency);
    }

    /**
     * Add an appliable dependency for this task item.
     *
     * @param appliable the appliable dependency.
     * @return the key to be used as parameter to taskResult(string) method to retrieve updated dependency
     */
    @SuppressWarnings("unchecked")
    protected String addeDependency(Appliable<? extends Indexable> appliable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) appliable;
        return this.addDependency(dependency);
    }

    /**
     * Add an executable dependency for this task item.
     *
     * @param executable the executable dependency
     * @return the key to be used as parameter to taskResult(string) method to retrieve result of executing
     * the executable dependency
     */
    @SuppressWarnings("unchecked")
    protected String addDependency(Executable<? extends Indexable> executable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) executable;
        return this.addDependency(dependency);
    }

    /**
     * Add a "post-run" dependent task item for this task item.
     *
     * @param dependent the "post-run" dependent task item.
     * @return key to be used as parameter to taskResult(string) method to retrieve result of root
     * task in the given dependent task group
     */
    public String addPostRunDependent(FunctionalTaskItem dependent) {
        Objects.requireNonNull(dependent);
        return this.taskGroup().addPostRunDependent(dependent);
    }

    /**
     * Add a "post-run" dependent for this task item.
     *
     * @param dependent the "post-run" dependent.
     * @return key to be used as parameter to taskResult(string) method to retrieve result of root
     * task in the given dependent task group
     */
    public String addPostRunDependent(TaskGroup.HasTaskGroup dependent) {
        Objects.requireNonNull(dependent);
        this.taskGroup().addPostRunDependentTaskGroup(dependent.taskGroup());
        return dependent.taskGroup().key();
    }

    /**
     * Add a creatable "post-run" dependent for this task item.
     *
     * @param creatable the creatable "post-run" dependent.
     * @return the key to be used as parameter to taskResult(string) method to retrieve created "post-run" dependent
     */
    @SuppressWarnings("unchecked")
    protected String addPostRunDependent(Creatable<? extends Indexable> creatable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) creatable;
        return this.addPostRunDependent(dependency);
    }

    /**
     * Add an appliable "post-run" dependent for this task item.
     *
     * @param appliable the appliable "post-run" dependent.
     * @return the key to be used as parameter to taskResult(string) method to retrieve updated "post-run" dependent
     */
    @SuppressWarnings("unchecked")
    protected String addPostRunDependent(Appliable<? extends Indexable> appliable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) appliable;
        return this.addPostRunDependent(dependency);
    }

    /**
     * Add an executable "post-run" dependent for this task item.
     *
     * @param executable the executable "post-run" dependent
     * @return the key to be used as parameter to taskResult(string) method to retrieve result of executing
     * the executable "post-run" dependent
     */
    @SuppressWarnings("unchecked")
    protected String addPostRunDependent(Executable<? extends Indexable> executable) {
        TaskGroup.HasTaskGroup dependency = (TaskGroup.HasTaskGroup) executable;
        return this.addPostRunDependent(dependency);
    }

    /**
     * Get result of one of the task that belongs to this task's task group.
     *
     * @param key the task key
     * @param <T> the actual type of the task result
     * @return the task result, null will be returned if task has not produced a result yet
     */
    @SuppressWarnings("unchecked")
    protected <T extends Indexable> T taskResult(String key) {
        Indexable result = this.taskGroup.taskResult(key);
        if (result == null) {
            return null;
        } else {
            T castedResult = (T) result;
            return castedResult;
        }
    }

    @Override
    public Indexable result() {
        return this.taskResult;
    }

    @Override
    public void beforeGroupInvoke() {
        // NOP
    }

    @Override
    public boolean isHot() {
        return false;
    }

    @Override
    public Observable<Indexable> invokeAsync(TaskGroup.InvocationContext context) {
        return this.invokeTaskAsync(context)
                .subscribeOn(SdkContext.getRxScheduler())
                .map(new Func1<Indexable, Indexable>() {
                    @Override
                    public Indexable call(Indexable result) {
                        taskResult = result;
                        return result;
                    }
                });
    }

    @Override
    public Completable invokeAfterPostRunAsync(boolean isGroupFaulted) {
        return Completable.complete();
    }

    protected abstract Observable<Indexable> invokeTaskAsync(TaskGroup.InvocationContext context);

    /**
     * @return an instance of {@link VoidIndexable} with key same as the key of this TaskItem.
     */
    protected Indexable voidIndexable() {
        return new VoidIndexable(this.key);
    }

    /**
     * @return an Observable upon subscription emits {@link VoidIndexable} with key same as the key of
     * this TaskItem
     */
    protected Observable<Indexable> voidObservable() {
        return Observable.just(this.voidIndexable());
    }
}