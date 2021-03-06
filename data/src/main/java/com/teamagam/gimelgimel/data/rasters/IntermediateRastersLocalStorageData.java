package com.teamagam.gimelgimel.data.rasters;


import com.teamagam.gimelgimel.data.common.ExternalDirProvider;
import com.teamagam.gimelgimel.data.config.Constants;
import com.teamagam.gimelgimel.domain.rasters.IntermediateRastersLocalStorage;
import com.teamagam.gimelgimel.domain.rasters.entity.IntermediateRaster;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import javax.inject.Inject;

public class IntermediateRastersLocalStorageData implements IntermediateRastersLocalStorage {

    private static final String TPK_SUFFIX = ".tpk";
    private static final String EMPTY_STRING = "";

    private final File mRastersDir;

    @Inject
    public IntermediateRastersLocalStorageData(ExternalDirProvider externalDirProvider) {
        mRastersDir = new File(
                externalDirProvider.getExternalFilesDir() +
                        File.separator +
                        Constants.RASTERS_CACHE_DIR_NAME);
    }

    @Override
    public Iterable<IntermediateRaster> getExistingRasters() {
        return transformToRaster(getFiles());
    }

    private Iterable<IntermediateRaster> transformToRaster(File[] files) {
        ArrayList<IntermediateRaster> rasters = new ArrayList<>();

        for (File file : files) {
            rasters.add(transformToRaster(file));
        }

        return rasters;
    }

    private IntermediateRaster transformToRaster(File file) {
        String name = file.getName().replace(TPK_SUFFIX, EMPTY_STRING);
        URI uri = file.toURI();

        return new IntermediateRaster(name, uri);
    }

    private File[] getFiles() {
        File[] files = mRastersDir.listFiles(pathname -> {
            return pathname.getName().endsWith(TPK_SUFFIX);
        });
        return files == null ? new File[0] : files;
    }
}
