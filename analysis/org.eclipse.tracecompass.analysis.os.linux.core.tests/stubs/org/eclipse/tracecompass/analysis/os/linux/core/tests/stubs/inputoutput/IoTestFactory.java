/*******************************************************************************
 * Copyright (c) 2016 École Polytechnique de Montréal
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.tracecompass.analysis.os.linux.core.tests.stubs.inputoutput;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.tracecompass.analysis.os.linux.core.inputoutput.Attributes;
import org.eclipse.tracecompass.analysis.os.linux.core.inputoutput.StateValues;
import org.eclipse.tracecompass.analysis.os.linux.core.tests.stubs.LinuxTestCase;
import org.eclipse.tracecompass.statesystem.core.interval.ITmfStateInterval;
import org.eclipse.tracecompass.statesystem.core.statevalue.TmfStateValue;
import org.eclipse.tracecompass.statesystem.core.tests.shared.utils.StateSystemTestUtils;

import com.google.common.collect.ImmutableList;

import org.eclipse.tracecompass.statesystem.core.tests.shared.utils.StateIntervalStub;

/**
 * Factory of test for the input output analysis
 *
 * @author Geneviève Bastien
 */
public final class IoTestFactory {

    private static final String DEVICE_ID = "8388624";
    private static final String DEVICE_NAME = "sda";
    private static final String SECOND_DEVICE_ID = "8388608";

    private IoTestFactory() {

    }

    /**
     * This test case covers cases of simple requests to disk, insert, issue and
     * completion:
     *
     * <pre>
     * - Write Request inserted at 5L, issued at 10L and completed at 20L
     * - Two write requests are issued at 25L, one issued at 27L and completed at 30L, the other issued at 29L and completed at 35L
     * - Read request inserted at 40L, issued at 50L and completed at 60L
     * </pre>
     */
    public final static LinuxTestCase SIMPLE_REQUESTS = new LinuxTestCase("io_analysis.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Driver and waiting queue length */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 9, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(10, 24, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(25, 26, TmfStateValue.newValueInt(2)),
                    new StateIntervalStub(27, 28, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(29, 39, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(40, 49, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(50, 60, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE_LENGTH));

            intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 9, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(10, 19, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(20, 26, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(27, 28, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(29, 29, TmfStateValue.newValueInt(2)),
                    new StateIntervalStub(30, 34, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(35, 49, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(50, 59, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(60, 60, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE_LENGTH));

            /* Bytes read and written */
            intervals = ImmutableList.of(new StateIntervalStub(1, 59, TmfStateValue.nullValue()),
                    new StateIntervalStub(60, 60, TmfStateValue.newValueInt(256)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 19, TmfStateValue.nullValue()),
                    new StateIntervalStub(20, 29, TmfStateValue.newValueInt(8)),
                    new StateIntervalStub(30, 34, TmfStateValue.newValueInt(16)),
                    new StateIntervalStub(35, 60, TmfStateValue.newValueInt(24)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_WRITTEN));

            intervals = ImmutableList.of(new StateIntervalStub(1, 60, TmfStateValue.newValueString(DEVICE_NAME)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID));
            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            Set<PunctualInfo> info = new HashSet<>();

            PunctualInfo oneInfo = new PunctualInfo(5L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(444L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(10L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(444L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.newValueInt(0));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(20L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.nullValue());
            info.add(oneInfo);

            oneInfo = new PunctualInfo(25L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(111L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(222L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(27L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(222L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(111L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.newValueInt(0));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(29L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(111L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.newValueInt(0));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(222L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.ISSUED_FROM), TmfStateValue.newValueInt(1));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(30L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(222L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.ISSUED_FROM), TmfStateValue.newValueInt(1));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(35L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "1", Attributes.ISSUED_FROM), TmfStateValue.nullValue());
            info.add(oneInfo);

            oneInfo = new PunctualInfo(40L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(444L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(256));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(50L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(444L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(256));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.newValueInt(0));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(60L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.nullValue());
            info.add(oneInfo);

            return info;
        }

    };

    /**
     * This test case tests the behavior of a simple request without the device
     * statedump
     *
     * <pre>
     * - Write Request inserted at 5L, issued at 10L and completed at 20L
     * </pre>
     */
    public final static LinuxTestCase SIMPLE_NO_STATEDUMP = new LinuxTestCase("io_nostatedump.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Driver and waiting queue length */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(5, 9, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(10, 20, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE_LENGTH));

            intervals = ImmutableList.of(new StateIntervalStub(5, 9, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(10, 19, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(20, 20, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE_LENGTH));

            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            return new HashSet<>();
        }
    };

    /**
     * This test case contains system call read events
     */
    public final static LinuxTestCase SYSCALL_READ = new LinuxTestCase("io_syscall_read.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Bytes read for a given process */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(1, 9, TmfStateValue.nullValue()),
                    new StateIntervalStub(10, 24, TmfStateValue.newValueInt(8)),
                    new StateIntervalStub(25, 50, TmfStateValue.newValueInt(44)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "2", Attributes.BYTES_READ));

            /* Bytes read for a given process */
            intervals = ImmutableList.of(new StateIntervalStub(1, 49, TmfStateValue.nullValue()),
                    new StateIntervalStub(50, 50, TmfStateValue.newValueInt(8)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "5", Attributes.BYTES_READ));

            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            return new HashSet<>();
        }
    };

    /**
     * This test case contains system call write events
     */
    public final static LinuxTestCase SYSCALL_WRITE = new LinuxTestCase("io_syscall_write.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Bytes read for a given process */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(1, 9, TmfStateValue.nullValue()),
                    new StateIntervalStub(10, 29, TmfStateValue.newValueInt(16)),
                    new StateIntervalStub(30, 39, TmfStateValue.newValueInt(26)),
                    new StateIntervalStub(40, 50, TmfStateValue.newValueInt(36)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "2", Attributes.BYTES_WRITTEN));

            /* Bytes read for a given process */
            intervals = ImmutableList.of(new StateIntervalStub(1, 29, TmfStateValue.nullValue()),
                    new StateIntervalStub(30, 49, TmfStateValue.newValueInt(32)),
                    new StateIntervalStub(50, 50, TmfStateValue.newValueInt(96)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "5", Attributes.BYTES_WRITTEN));

            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            return new HashSet<>();
        }
    };

    /**
     * This test case contains requests merges with different events
     *
     * <pre>
     * - Write request inserted at 5L, frontmerged at 7L, issued at 10L and completed 20L
     * - 4 reqd requests inserted at 15L, 35L, 40L and 50L, merged at 42L, 45L and 55L, issued at 60L and completed at 65L
     * </pre>
     */
    public final static LinuxTestCase REQUESTS_MERGE = new LinuxTestCase("io_req_merge.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Driver and waiting queue length */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 9, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(10, 14, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(15, 34, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(35, 39, TmfStateValue.newValueInt(2)),
                    new StateIntervalStub(40, 41, TmfStateValue.newValueInt(3)),
                    new StateIntervalStub(42, 44, TmfStateValue.newValueInt(2)),
                    new StateIntervalStub(45, 49, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(50, 54, TmfStateValue.newValueInt(2)),
                    new StateIntervalStub(55, 59, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(60, 65, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE_LENGTH));

            intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 9, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(10, 19, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(20, 59, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(60, 64, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(65, 65, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE_LENGTH));

            /* Bytes read and written */
            intervals = ImmutableList.of(new StateIntervalStub(1, 64, TmfStateValue.nullValue()),
                    new StateIntervalStub(65, 65, TmfStateValue.newValueInt(40)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 19, TmfStateValue.nullValue()),
                    new StateIntervalStub(20, 65, TmfStateValue.newValueInt(16)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_WRITTEN));

            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            Set<PunctualInfo> info = new HashSet<>();

            PunctualInfo oneInfo = new PunctualInfo(5L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(444L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(7L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(436L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(16));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(10L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(436L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(16));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(15L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(292L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), StateValues.WRITING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(436L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(16));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(20L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            info.add(oneInfo);

            oneInfo = new PunctualInfo(35L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(292L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(284L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(40L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(292L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(284L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(300L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(16));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(42L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(292L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(24));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.MERGED_IN), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(284L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.MERGED_IN), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.MERGED_IN), TmfStateValue.newValueInt(0));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(45L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.MERGED_IN), TmfStateValue.newValueInt(1));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(284L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(32));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.MERGED_IN), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.MERGED_IN), TmfStateValue.newValueInt(0));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(50L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(316L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(8));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.MERGED_IN), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(284L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(32));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.MERGED_IN), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.MERGED_IN), TmfStateValue.newValueInt(0));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(55L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.MERGED_IN), TmfStateValue.newValueInt(1));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(284L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(40));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.MERGED_IN), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.MERGED_IN), TmfStateValue.newValueInt(0));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(60L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "1", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE, "2", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());

            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), StateValues.READING_REQUEST_VALUE);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.newValueLong(284L));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.newValueInt(40));
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.newValueInt(1));
            info.add(oneInfo);

            oneInfo = new PunctualInfo(65L);
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0"), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.CURRENT_REQUEST), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.REQUEST_SIZE), TmfStateValue.nullValue());
            oneInfo.addValue(StateSystemTestUtils.makeAttribute(Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE, "0", Attributes.ISSUED_FROM), TmfStateValue.nullValue());
            info.add(oneInfo);

            return info;
        }
    };

    /**
     * This test case contains special cases when some events are missed (no
     * insert, no issue, merge without prior insertion)
     *
     * <pre>
     * - write request issued at 5L and completed at 10L
     *     * expected: See the request in the issue queue
     * - frontmerge with a request that was not inserted at 15L, issued 20L, completed 25L
     *     * expected: See only that one request with the right sector, issued and completed normally
     * - read request completed at 30L, never insert or issued
     *     * expected: Sectors added to the total of read sectors
     * - elv_merge after a request not inserted: insert at 35, merge at 38, issued at 40, completed at 45
     *     * expected: See only one request with the right sector, issued and completed normally
     * - elv merge before a request not inserted: insert at 50, merge at 55, issued at 60 and completed at 65
     *     * expected: See only one request with the right sector, issued and completed normally
     * - request that does not complete: insert at 80, issued at 100
     *     * expected: have an open request in the driving queue until the end
     * - request that is not issued: insert at 120, completed at 140
     *     * expected: have an open request in the waiting queue until the end, written sectors at completion are counted
     * </pre>
     */
    public final static LinuxTestCase REQUESTS_MISSING = new LinuxTestCase("io_missing.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Driver and waiting queue length */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 14, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(15, 19, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(20, 34, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(35, 39, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(40, 49, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(50, 59, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(60, 79, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(80, 99, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(100, 119, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(120, 140, TmfStateValue.newValueInt(1)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE_LENGTH));

            intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 9, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(10, 19, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(20, 24, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(25, 39, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(40, 44, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(45, 59, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(60, 64, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(65, 99, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(100, 140, TmfStateValue.newValueInt(1)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE_LENGTH));

            /* Bytes read and written */
            intervals = ImmutableList.of(new StateIntervalStub(1, 29, TmfStateValue.nullValue()),
                    new StateIntervalStub(30, 44, TmfStateValue.newValueInt(16)),
                    new StateIntervalStub(45, 64, TmfStateValue.newValueInt(56)),
                    new StateIntervalStub(65, 140, TmfStateValue.newValueInt(96)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 9, TmfStateValue.nullValue()),
                    new StateIntervalStub(10, 24, TmfStateValue.newValueInt(8)),
                    new StateIntervalStub(25, 139, TmfStateValue.newValueInt(24)),
                    new StateIntervalStub(140, 140, TmfStateValue.newValueInt(32)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_WRITTEN));

            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            return new HashSet<>();
        }
    };

    /**
     * This test case tests that requests are associated with the right device
     *
     * <pre>
     * - write request on sda inserted at 5L, issued at 10L and completed at 20L
     * - read request on sdb inserted at 8L (before statedump), issued at 12L and completed at 18L
     * </pre>
     */
    public final static LinuxTestCase TWO_DEVICES = new LinuxTestCase("io_two_devices.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Driver and waiting queue length */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 9, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(10, 20, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.WAITING_QUEUE_LENGTH));

            intervals = ImmutableList.of(new StateIntervalStub(1, 4, TmfStateValue.nullValue()),
                    new StateIntervalStub(5, 9, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(10, 19, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(20, 20, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.DRIVER_QUEUE_LENGTH));

            /* Bytes read and written */
            intervals = ImmutableList.of(new StateIntervalStub(1, 20, TmfStateValue.nullValue()));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 19, TmfStateValue.nullValue()),
                    new StateIntervalStub(20, 20, TmfStateValue.newValueInt(8)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, DEVICE_ID, Attributes.SECTORS_WRITTEN));

            /* Driver and waiting queue length */
            intervals = ImmutableList.of(new StateIntervalStub(1, 7, TmfStateValue.nullValue()),
                    new StateIntervalStub(8, 11, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(12, 20, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, SECOND_DEVICE_ID, Attributes.WAITING_QUEUE_LENGTH));

            intervals = ImmutableList.of(new StateIntervalStub(1, 7, TmfStateValue.nullValue()),
                    new StateIntervalStub(8, 11, TmfStateValue.newValueInt(0)),
                    new StateIntervalStub(12, 17, TmfStateValue.newValueInt(1)),
                    new StateIntervalStub(18, 20, TmfStateValue.newValueInt(0)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, SECOND_DEVICE_ID, Attributes.DRIVER_QUEUE_LENGTH));

            /* Bytes read and written */
            intervals = ImmutableList.of(new StateIntervalStub(1, 17, TmfStateValue.nullValue()),
                    new StateIntervalStub(18, 20, TmfStateValue.newValueInt(16)));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, SECOND_DEVICE_ID, Attributes.SECTORS_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 20, TmfStateValue.nullValue()));
            info.add(new IntervalInfo(intervals, Attributes.DISKS, SECOND_DEVICE_ID, Attributes.SECTORS_WRITTEN));

            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            return new HashSet<>();
        }
    };

    /**
     * This test case contains system call read and write events but instead of
     * context with the event, it has sched_switches so that the kernel analysis
     * has to be used to get the running threads
     */
    public final static LinuxTestCase SYSCALLS_KERNEL = new LinuxTestCase("io_full_kernel.xml") {

        @Override
        public Set<IntervalInfo> getTestIntervals() {
            Set<IntervalInfo> info = new HashSet<>();

            /* Bytes read and written for thread 1 */
            ImmutableList<ITmfStateInterval> intervals = ImmutableList.of(new StateIntervalStub(1, 29, TmfStateValue.nullValue()),
                    new StateIntervalStub(30, 45, TmfStateValue.newValueInt(20)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "1", Attributes.BYTES_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 45, TmfStateValue.nullValue()));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "1", Attributes.BYTES_WRITTEN));

            /* Bytes read and written for thread 2 */
            intervals = ImmutableList.of(new StateIntervalStub(1, 9, TmfStateValue.nullValue()),
                    new StateIntervalStub(10, 45, TmfStateValue.newValueInt(8)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "2", Attributes.BYTES_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 45, TmfStateValue.nullValue()));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "2", Attributes.BYTES_WRITTEN));

            /* Bytes read and written for thread 3 */
            intervals = ImmutableList.of(new StateIntervalStub(1, 34, TmfStateValue.nullValue()),
                    new StateIntervalStub(35, 45, TmfStateValue.newValueInt(36)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "3", Attributes.BYTES_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 39, TmfStateValue.nullValue()),
                    new StateIntervalStub(40, 45, TmfStateValue.newValueInt(64)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "3", Attributes.BYTES_WRITTEN));

            /* Bytes read and written for thread 4 */
            intervals = ImmutableList.of(new StateIntervalStub(1, 45, TmfStateValue.nullValue()));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "4", Attributes.BYTES_READ));

            intervals = ImmutableList.of(new StateIntervalStub(1, 9, TmfStateValue.nullValue()),
                    new StateIntervalStub(10, 45, TmfStateValue.newValueInt(16)));
            info.add(new IntervalInfo(intervals, Attributes.THREADS, "4", Attributes.BYTES_WRITTEN));

            return info;
        }

        @Override
        public Set<PunctualInfo> getPunctualTestData() {
            return new HashSet<>();
        }
    };
}