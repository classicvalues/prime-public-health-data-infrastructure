from azure.storage.blob import BlobServiceClient
import os

# This file runs before all tests. More info: https://docs.pytest.org/en/latest/how-to/writing_plugins.html#conftest-py-plugins and https://docs.microsoft.com/en-us/azure/storage/blobs/use-azurite-to-run-automated-tests


def pytest_generate_tests(metafunc):
    os.environ[
        "AZURE_STORAGE_CONNECTION_STRING"
    ] = "DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:10000/devstoreaccount1;"

    os.environ["STORAGE_CONTAINER"] = "test-container"

    # Initial run to create container
    blob_service_client = BlobServiceClient.from_connection_string(
        os.environ.get("AZURE_STORAGE_CONNECTION_STRING")
    )
    try:
        blob_service_client.create_container(os.environ.get("STORAGE_CONTAINER"))
    except Exception as e:
        print(e)
