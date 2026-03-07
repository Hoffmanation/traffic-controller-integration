-- controllers table will represent the physical traffic controller device
CREATE TABLE controllers (
    id            BIGSERIAL    PRIMARY KEY,
    controller_id VARCHAR(128) NOT NULL UNIQUE,
    description   VARCHAR(256),
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_controllers_controller_id ON controllers (controller_id);

-- controller_status table will represent historical snapshot of every status poll
CREATE TABLE controller_status (
    id               BIGSERIAL    PRIMARY KEY,
    controller_id    VARCHAR(128) NOT NULL REFERENCES controllers (controller_id) ON DELETE CASCADE,
    state            VARCHAR(32)  NOT NULL,
    program          VARCHAR(32),
    errors_json      JSONB        DEFAULT '[]'::jsonb,
    device_timestamp TIMESTAMPTZ,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_status_controller_id ON controller_status (controller_id);
CREATE INDEX idx_status_created_at   ON controller_status (created_at DESC);

-- detector_readings table will repreasent history for trend analysis
CREATE TABLE detector_readings (
    id               BIGSERIAL    PRIMARY KEY,
    controller_id    VARCHAR(128) NOT NULL REFERENCES controllers (controller_id) ON DELETE CASCADE,
    detector_number  INTEGER      NOT NULL,
    detector_name    VARCHAR(32)  NOT NULL,
    vehicle_count    INTEGER      NOT NULL,
    occupancy        DOUBLE PRECISION NOT NULL,
    device_timestamp TIMESTAMPTZ  NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_det_controller_id ON detector_readings (controller_id);
CREATE INDEX idx_det_detector_name ON detector_readings (detector_name);
CREATE INDEX idx_det_device_ts     ON detector_readings (device_timestamp DESC);

-- commands table will represent audit trail of every command dispatched to a controller
CREATE TABLE commands (
    id               BIGSERIAL    PRIMARY KEY,
    controller_id    VARCHAR(128) NOT NULL REFERENCES controllers (controller_id) ON DELETE CASCADE,
    command          VARCHAR(256) NOT NULL,
    status           VARCHAR(16)  NOT NULL,
    result_value     VARCHAR(128),
    device_timestamp TIMESTAMPTZ,
    created_at          TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_cmd_controller_id ON commands (controller_id);
CREATE INDEX idx_cmd_created_at       ON commands (created_at DESC);