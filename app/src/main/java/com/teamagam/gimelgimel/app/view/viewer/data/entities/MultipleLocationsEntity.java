package com.teamagam.gimelgimel.app.view.viewer.data.entities;

import com.teamagam.gimelgimel.app.view.viewer.data.geometries.Geometry;
import com.teamagam.gimelgimel.app.view.viewer.data.geometries.MultiPointGeometry;

/**
 * An abstract class for entities who's geometry is
 * a collection of locations (i.e. polylines, polygons)
 */
public abstract class MultipleLocationsEntity<E extends MultipleLocationsEntity> extends AbsEntity{

    private MultiPointGeometry mPointsGeometry;

    public MultipleLocationsEntity(String id, MultiPointGeometry pointsGeometry) {
        super(id);
        mPointsGeometry = pointsGeometry;
    }

    @Override
    public Geometry getGeometry() {
        return mPointsGeometry;
    }

    @Override
    public void updateGeometry(Geometry geo) {
        if (!(geo instanceof MultiPointGeometry)) {
            throw new UnsupportedOperationException(
                    "Given geometry is not supported for entities of type " + this.getClass().getSimpleName());
        }

        mPointsGeometry = (MultiPointGeometry) geo;
        fireEntityChanged();
    }

    public abstract class Builder<E extends MultipleLocationsEntity> extends AbsEntity.Builder {

        public Builder(String id) {
            super(id);
        }

        @Override
        public E.Builder setGeometry(Geometry geometry) {
            mPointsGeometry = (MultiPointGeometry) geometry;
            return this;
        }
    }
}
