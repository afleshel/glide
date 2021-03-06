package com.bumptech.glide.load.data;

import android.content.res.AssetManager;
import android.util.Log;

import com.bumptech.glide.Logs;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;

import java.io.IOException;

/**
 * An abstract class for obtaining data for an asset path using an {@link
 * android.content.res.AssetManager}.
 *
 * @param <T> The type of data obtained from the asset path (InputStream, FileDescriptor etc).
 */
public abstract class AssetPathFetcher<T> implements DataFetcher<T> {
  private final String assetPath;
  private final AssetManager assetManager;
  private T data;

  public AssetPathFetcher(AssetManager assetManager, String assetPath) {
    this.assetManager = assetManager;
    this.assetPath = assetPath;
  }

  @Override
  public void loadData(Priority priority, DataCallback<? super T> callback) {
    try {
      data = loadResource(assetManager, assetPath);
    } catch (IOException e) {
      if (Logs.isEnabled(Log.DEBUG)) {
        Logs.log(Log.DEBUG, "Failed to load data from asset manager", e);
      }
    }
    callback.onDataReady(data);
  }

  @Override
  public void cleanup() {
    if (data == null) {
      return;
    }
    try {
      close(data);
    } catch (IOException e) {
      // Ignored.
    }
  }

  @Override
  public void cancel() {
    // Do nothing.
  }

  @Override
  public DataSource getDataSource() {
    return DataSource.LOCAL;
  }

  /**
   * Opens the given asset path with the given {@link android.content.res.AssetManager} and returns
   * the conrete data type returned by the AssetManager.
   *
   * @param assetManager An AssetManager to use to open the given path.
   * @param path         A string path pointing to a resource in assets to open.
   */
  protected abstract T loadResource(AssetManager assetManager, String path) throws IOException;

  /**
   * Closes the concrete data type if necessary.
   *
   * @param data The data to close.
   * @throws IOException
   */
  protected abstract void close(T data) throws IOException;
}
