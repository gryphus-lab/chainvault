import os
import json
import shutil
import tempfile
import random
from datetime import datetime, timedelta
import numpy as np
import tifffile as tf
from PIL import Image, ImageDraw, ImageFont
from concurrent.futures import ProcessPoolExecutor

DEST_DIR = "./static/payloads/"
TOTAL_BUNDLES = 10
COMPANIES = ["Acme Corp", "Globex", "Soylent Corp", "Initech", "Umbrella Corp", 
             "Stark Industries", "Wayne Ent", "Hooli", "Cyberdyne", "Wonka Ind"]

import numpy as np
import tifffile as tf
from PIL import Image, ImageDraw, ImageFont

def create_random_tiff(args):
    file_path, seed = args
    img_w, img_h = (1024, 1024) 
    rng = np.random.default_rng(seed)
    
    sample_text = os.path.basename(file_path)
    
    data = rng.integers(0, 256, size=(img_h, img_w), dtype=np.uint8)
    img = Image.fromarray(data)
    draw = ImageDraw.Draw(img)
    
    font_size = 80
    try:
        font = ImageFont.truetype("arialbd.ttf", font_size) 
    except IOError:
        font = ImageFont.load_default()

    bbox = draw.textbbox((0, 0), sample_text, font=font)
    text_w = bbox[2] - bbox[0]
    text_h = bbox[3] - bbox[1]
    
    x = (img_w - text_w) // 2
    y = (img_h - text_h) // 2
    
    pad = 20
    bg_coords = [x - pad, y - pad, x + text_w + pad, y + text_h + pad]
    draw.rectangle(bg_coords, fill=255) 
    
    draw.text((x, y), sample_text, fill=0, font=font) 
    
    tf.imwrite(file_path, np.array(img), compression='zlib')
    return file_path

def generate_random_metadata(doc_id, zip_filename):
    dt = datetime(2025, 1, 1) + timedelta(days=random.randint(0, 30), seconds=random.randint(0, 86400))
    client_id = f"CHE-{random.randint(100, 999)}.{random.randint(100, 999)}.{random.randint(100, 999)}"
    account_no = f"CH{random.randint(10, 99)}007620{random.randint(10**10, 10**11 - 1)}"

    return {
        "id": doc_id,
        "docId": doc_id,
        "title": f"Invoice #{random.randint(1000, 9999)} - {random.choice(COMPANIES)}",
        "creationDate": dt.strftime("%Y-%m-%dT%H:%M:%SZ"),
        "clientId": client_id,
        "accountNo": account_no,
        "documentType": "INVOICE",
        "department": "Accounts Payable",
        "status": "ARCHIVED",
        "originalSizeBytes": 5 * 1024 * 1024,
        "pageCount": 5,
        "tags": ["finance", "2025", "batch"],
        "payloadUrl": f"/payloads/{zip_filename}"
    }

def create_bundle(bundle_index):
    # Format the 3-digit suffix
    suffix = f"{bundle_index:03d}"
    doc_id = f"DOC-ARCH-2025-{suffix}"
    
    # New zip name format: invoice_001
    bundle_name = f"invoice_{suffix}"
    zip_filename = f"{bundle_name}.zip"
    
    with tempfile.TemporaryDirectory() as tmp_dir:
        tasks = []
        for i in range(1, 6): 
            filename = f"{doc_id}_{i}.tif"
            file_path = os.path.join(tmp_dir, filename)
            seed = bundle_index * 10 + i
            tasks.append((file_path, seed))

        with ProcessPoolExecutor(max_workers=4) as executor:
            list(executor.map(create_random_tiff, tasks))
        
        local_zip_path = shutil.make_archive(bundle_name, 'zip', tmp_dir)
        shutil.move(local_zip_path, os.path.join(DEST_DIR, zip_filename))
    
    return generate_random_metadata(doc_id, zip_filename)

def main():
    # 1. Generate requirements.txt on the fly
    requirements = ["numpy", "tifffile", "imagecodecs"]
    with open("requirements.txt", "w") as f:
        f.write("\n".join(requirements))
    
    # 2. Ensure destination exists
    os.makedirs(DEST_DIR, exist_ok=True)
    
    all_metadata = []
    print(f"Generating {TOTAL_BUNDLES} bundles into {DEST_DIR}...")
    
    for i in range(1, TOTAL_BUNDLES + 1):
        meta = create_bundle(i)
        all_metadata.append(meta)
        print(f"[{i:03d}] Generated {meta['payloadUrl']}")

    # 3. Write final db.json
    with open("db.json", "w") as f:
        json.dump({"documents": all_metadata}, f, indent=2)
    
    print("\nSuccess. 'db.json' and 'requirements.txt' created.")


if __name__ == "__main__":
    main()
